<template>
  <LayoutTwoColumn class="team-detail">
    <template v-slot:center>
      <q-dialog persistent v-model="flagShowInviteDialog">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-weight-bold">邀请加入团队</div>
            <div class="text-grey">请将以下链接发送给被邀请成员。成员加入后，可以访问工作空间的所有数据。一个邀请链接只能使用一次，有效期为24小时</div>
          </q-card-section>

          <q-card-section class="bg-grey-2" style="word-break: break-all;">
            <template v-if="inviteLoading">
              <q-skeleton type="text" animation="fade" />
              <q-skeleton type="text" animation="fade" />
            </template>
            <span v-else>{{ inviteLink }}</span>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="关闭" v-close-popup />
            <q-btn unelevated label="复制" color="primary" @click="onClickCopyInviteLink" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-input dense outlined class="full-width" bg-color="white" v-model="username" placeholder="搜索成员" @change="onClickSearchMember">
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearchMember"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="邀请成员" @click="onClickInviteMember" />
              </template>
            </q-input>
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <div class="page-heading">我的团队</div>
          <q-table
            flat
            class="q-mt-sm"
            :rows="members"
            :columns="memberColumns"
            v-model:pagination="memberPagination"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="teamMemberId"
            @request="onMemberTableSearch"
            :loading="memberLoading"
            no-data-label="没有团队成员"
          >
            <template v-slot:body-cell-username="props">
              <q-td :props="props">
                <q-icon class="vertical-middle q-mr-xs" name="person" size="xs" />
                <span class="vertical-middle">{{ props.value }}</span>
              </q-td>
            </template>
            <template v-slot:body-cell-joinTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-action="props">
              <q-td :props="props">
                <q-btn color="negative" flat dense icon="delete" @click="onClickDeleteMember(props.row.username, props.row.userId)">
                  <q-tooltip>删除</q-tooltip>
                </q-btn>
              </q-td>
            </template>
          </q-table>

          <div class="page-heading q-mt-md">已加入的团队</div>
          <q-table
            flat
            class="q-mt-sm"
            :rows="teams"
            :columns="teamColumns"
            v-model:pagination="teamPagination"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="teamId"
            @request="onTeamTableSearch"
            :loading="teamLoading"
            no-data-label="未加入其他团队"
          >
            <template v-slot:body-cell-username="props">
              <q-td :props="props">
                <span>{{ props.value }} 团队</span>
              </q-td>
            </template>
            <template v-slot:body-cell-joinTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-action="props">
              <q-td :props="props">
                <q-btn color="negative" flat dense icon="exit_to_app" @click="onClickLeaveTeam(props.row.username, props.row.teamId)">
                  <q-tooltip>离开</q-tooltip>
                </q-btn>
              </q-td>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss" scoped>
.team-detail{

}
</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { copyToClipboard } from 'quasar'
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

const memberColumns = [
  { name: 'username', label: '成员', field: 'username', align: 'left', sortable: true, headerStyle: "width: 45%;" },
  { name: 'joinTime', label: '加入时间', field: 'joinTime', align: 'left', sortable: true  },
  { name: 'action', label: '操作', sortable: false, headerStyle: "width: 5%;" },
]

const teamColumns = [
  { name: 'username', label: '团队', field: 'username', align: 'left', sortable: true, headerStyle: "width: 45%;" },
  { name: 'joinTime', label: '加入时间', field: 'joinTime', align: 'left', sortable: true },
  { name: 'action', label: '操作', sortable: false, headerStyle: "width: 5%;" },
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const flagShowInviteDialog = ref(false)
    const joinTokenHashed = ref('')
    const inviteLoading = ref(false)
    const inviteLink = computed(() => {
      return `【Y20持续部署】${store.getters.username} 邀请您加入团队！${window.location.protocol}//${window.location.host}/nav/user/team/join?joinTokenHashed=${joinTokenHashed.value}`
    })

    const teamId = ref(null)

    const username = ref('')
    const members = ref([])
    const memberLoading = ref(false)
    const memberPagination = ref({
      sortBy: 'username',
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const teams = ref([])
    const teamLoading = ref(false)
    const teamPagination = ref({
      sortBy: 'username',
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const searchMember = () => {
      memberLoading.value = true
      authApi.listTeamMember({ 
        username: username.value,
        orderField: memberPagination.value.sortBy ? memberPagination.value.sortBy : "", 
        orderType: memberPagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: memberPagination.value.page, 
        pageSize: memberPagination.value.rowsPerPage
      }).then(resp => {
        members.value = resp.data.page
        memberPagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError('查询团队成员发生错误')
      }).finally(() => {
        memberLoading.value = false
      })
    }

    const getTeam = () => {
      memberLoading.value = true
      authApi.getTeam().then(resp => {
        const data = resp.data
        teamId.value = data.id

        searchMember()
      }, resp => {
        qUtil.notifyError('查询我的团队发生错误')
        memberLoading.value = false
      })
    }

    const searchTeam = () => {
      teamLoading.value = true
      authApi.listJoinedTeam({ 
        orderField: teamPagination.value.sortBy ? teamPagination.value.sortBy : "", 
        orderType: teamPagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: teamPagination.value.page, 
        pageSize: teamPagination.value.rowsPerPage
      }).then(resp => {
        teams.value = resp.data.page
        teamPagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError('查询已加入的团队发生错误')
      }).finally(() => {
        teamLoading.value = false
      })
    }

    onMounted(() => {
      getTeam()
      searchTeam()
    })

    return {
      dayjs,

      flagShowInviteDialog,
      joinTokenHashed,
      inviteLink,
      inviteLoading,

      teamId,
      username,
      members,
      teams,

      memberColumns,
      memberPagination,
      memberLoading,

      teamColumns,
      teamPagination,
      teamLoading,

      onClickSearchMember(){
        memberPagination.value.page = 1
        searchMember()
      },

      onMemberTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        memberPagination.value = props.pagination
        searchMember()
      },

      onClickDeleteMember(memberUsername, memberUserId){
        $q.dialog({
          title: '删除',
          message: `确定删除成员 ${memberUsername} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          authApi.deleteTeamMember({
            memberUserId,
          }).then(resp => {
            searchMember()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

      onTeamTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        teamPagination.value = props.pagination
        searchTeam()
      },

      onClickLeaveTeam(username, leaveTeamId){
        $q.dialog({
          title: '离开',
          message: `确定离开 ${username} 团队？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          authApi.leaveTeam({
            leaveTeamId,
          }).then(resp => {
            searchTeam()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

      onClickInviteMember(){
        flagShowInviteDialog.value = true
        inviteLoading.value = true
        authApi.createTeamJoinToken().then(resp => {
          const hash = resp.data
          joinTokenHashed.value = hash
          inviteLoading.value = false
        }, resp => {
          qUtil.notifyError('生成邀请链接发生错误')
        })
        
      },

      onClickCopyInviteLink(){
        copyToClipboard(inviteLink.value).then(() => {
          qUtil.notifySuccess('已复制到粘贴板')
        }).catch(() => {
          qUtil.notifyError('复制到粘贴板发生错误')
        })
      },
    }
  },
}
</script>