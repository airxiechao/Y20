<template>
  <LayoutTwoColumn class="session-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <div class="page-heading">会话</div>
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-table
            class="session-list-table"
            :rows="sessions"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="id"
            :filter="filter"
            @request="onTableSearch"
            :loading="loading"
          >
            <template v-slot:body-cell-createIp="props">
              <q-td :props="props">
                <span>{{props.value}}</span>
                <span v-if="props.row.createLocation" class="text-grey q-ml-xs">{{props.row.createLocation}}</span>
              </q-td>
            </template>
            <template v-slot:body-cell-beginTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-endTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-flagCurrentSession="props">
              <q-td :props="props">
                <div>
                  <q-icon v-if="props.value" size="20px" color="green" name="check" />
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-action="props">
              <q-td :props="props">
                <q-btn v-if="!props.row.flagCurrentSession" color="negative" flat dense icon="delete" @click="onClickRevokeSession(props.row.id)">
                  <q-tooltip>撤销</q-tooltip>
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
.session-list{
  &-table{
    
  }
}
</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

const columns = [
  { name: 'createIp', label: '登录IP', field: 'createIp', sortable: false },
  { name: 'beginTime', label: '登录时间', field: 'beginTime', sortable: true },
  { name: 'endTime', label: '过期时间', field: 'endTime', sortable: true },
  { name: 'flagCurrentSession', label: '当前会话', field: 'flagCurrentSession' },
  { name: 'action', label: '操作' },
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

    const sessions = ref([])
    const loading = ref(false)

    const pagination = ref({
      sortBy: 'beginTime',
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const search = () => {
      loading.value = true
      authApi.listUserAccessToken({ 
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        loading.value = false
        sessions.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        loading.value = false
      })
    }

    onMounted(() => {
      search()
    })

    return {
      dayjs,

      sessions,

      columns,
      filter: ref(''),
      pagination,
      loading,

      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        search()
      },

      onClickRevokeSession(accessTokenId){
        $q.dialog({
          title: '撤销',
          message: `确定撤销会话？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          authApi.deleteAccessToken({
            accessTokenId
          }).then(resp => {
            pagination.value.page = 1
            search()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },
    }
  },
}
</script>