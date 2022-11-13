<template>
  <LayoutTwoColumn class="list-agent">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-input dense outlined class="full-width" bg-color="white" v-model="agentId" placeholder="搜索节点" @change="onClickSearch">
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="接入新节点" to="/workspace/agent/join" />
              </template>
            </q-input>
          </q-toolbar>
        </div>
        <div class="q-pa-xs page-content">
          <div class="row q-pl-xs q-pr-sm q-pb-sm">
            <div class="col self-center q-pr-sm">
              <q-chip square style="overflow-x: auto; overflow-y: hidden;">
                <q-avatar icon="bookmark" color="green" text-color="white" />
                <span>最新版本：</span>
                <q-skeleton v-if="loadingVersion" type="text" width="50px" />
                <span v-else>{{latestVersion || '-'}}</span>
                <span class="q-ml-md">发布时间：</span>
                <q-skeleton v-if="loadingVersion" type="text" width="50px" />
                <span v-else>{{releaseTime ? dayjs(releaseTime).format('YYYY-MM-DD HH:mm:ss') : '-'}}</span>
              </q-chip>
            </div>
            <div class="self-center">
              <q-toggle class="text-subtitle2" size="xs" v-model="flagDetail" label="详细" />
            </div>
          </div>
          <q-table
            grid
            :hide-bottom="loading"
            class="list-agent-table"
            :rows="agents"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[4, 8, 16, 32]"
            row-key="agentId"
            :filter="filter"
            @request="onTableSearch"
            :loading="loading"
          >
            <template v-slot:no-data>
              <div class="full-width">
                <div class="row q-mt-xs">
                  <div class="col-12 col-md-6 col-lg-3">
                    <q-card flat class="agent-card q-pa-xs">
                      <q-item>
                        <q-item-section avatar>
                          <q-avatar icon="computer" class="q-mr-sm text-grey" size="lg" style="background: #ECF2FF;" />
                        </q-item-section>

                        <q-item-section>
                          <q-item-label>
                            <span class="vertical-middle text-grey">没有节点？</span>
                            <q-btn class="q-mx-xs vertical-middle" color="primary" dense flat label="接入一个节点" to="/workspace/agent/join" />
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-card>
                  </div>
                </div>
              </div>
            </template>
            <template v-slot:loading>
              <div class="row">
                <div class="q-pa-xs col-12 col-md-6 col-lg-3" v-for="i in [1,2,3,4]" :key="i">
                  <q-card flat class="agent-card q-pa-xs">
                    <q-item>
                      <q-item-section avatar>
                        <q-skeleton type="QAvatar" animation="fade" size="25px" />
                      </q-item-section>

                      <q-item-section>
                        <q-item-label>
                          <q-skeleton type="text" animation="fade" />
                        </q-item-label>
                      </q-item-section>
                    </q-item>

                    <template v-if="flagDetail">
                      <q-card-section v-for="j in [1,2,3,4,5]" :key="j">
                        <q-skeleton type="text" width="50%" class="text-subtitle2" animation="fade" />
                        <q-skeleton type="text" class="text-subtitle2" animation="fade" />
                      </q-card-section>
                    </template>
                  </q-card>
                </div>
              </div>
            </template>
            <template v-slot:item="props">
              <div class="q-pa-xs col-12 col-md-6 col-lg-3">
                <q-card flat class="agent-card q-pa-xs full-height">
                  <div>
                    <q-badge v-if="props.row.flagUpgrading" color="warning" floating>升级中</q-badge>
                    <q-badge v-else-if="props.row.flagRestarting" color="warning" floating>重启中</q-badge>
                  </div>
                  <q-list>
                    <q-item>
                      <q-item-section avatar>
                        <q-avatar icon="computer" class="q-mr-sm text-primary" size="lg" style="background: #ECF2FF;" />
                      </q-item-section>
                      <q-item-section class="cursor-pointer" @click="onClickConfigAgent(props.row.agentId)">
                        <q-item-label lines="1">
                          <span class="title vertical-middle">
                            {{props.row.agentId}}
                            <!-- <q-tooltip anchor="top left" self="bottom left" :offset="[0, 8]">{{ props.row.agentId }}</q-tooltip> -->
                          </span>
                          <template v-if="props.row.running">
                            <span>
                              <q-spinner-facebook class="vertical-middle q-ml-xs" color="orange"></q-spinner-facebook>
                              <q-tooltip anchor="top left" self="bottom left" :offset="[-4, 8]">流水线运行中</q-tooltip>
                            </span>
                          </template>
                        </q-item-label>
                        <q-item-label v-if="!flagDetail" class="q-gutter-x-xs" lines="1">
                          <q-badge v-if="props.row.active" color="green">在线</q-badge>
                          <q-badge v-else color="red">离线</q-badge>
                          <q-badge v-if="latestVersion && props.row.version !== latestVersion" color="warning">可升级</q-badge>
                          <q-badge v-if="props.row.accessTokenEndTime && props.row.accessTokenEndTime <= new Date().getTime()" class="vertical-middle q-ml-xs" color="warning">令牌过期</q-badge>
                          <q-badge v-else-if="props.row.accessTokenEndTime && props.row.accessTokenEndTime - new Date().getTime() < 30*24*60*60*1000" class="vertical-middle q-ml-xs" color="warning">即将过期</q-badge>
                        </q-item-label>
                      </q-item-section>
                      <q-item-section side>
                        <q-btn flat dense round icon="more_vert">
                          <q-menu>
                            <q-list style="min-width: 100px">
                              <q-item clickable v-close-popup @click="onClickConfigAgent(props.row.agentId)">
                                <q-item-section>配置</q-item-section>
                              </q-item>
                              <q-separator />
                              <q-item clickable v-close-popup @click="onClickRestartAgent(props.row.agentId)">
                                <q-item-section>重启服务</q-item-section>
                              </q-item>
                              <q-item clickable v-close-popup @click="onClickUpgradeAgent(props.row.agentId)">
                                <q-item-section>升级</q-item-section>
                              </q-item>
                              <q-separator />
                              <q-item clickable v-close-popup @click="onClickCleanAgent(props.row.agentId)">
                                <q-item-section>清理</q-item-section>
                              </q-item>
                              <q-item clickable v-close-popup @click="onClickUninstallAgent(props.row.agentId)">
                                <q-item-section class="text-negative">卸载</q-item-section>
                              </q-item>
                            </q-list>
                          </q-menu>
                        </q-btn>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>状态</span>
                        </q-item-label>
                        <q-item-label>
                          <q-badge  v-if="props.row.active" color="green">在线</q-badge >
                          <q-badge  v-else color="red">离线</q-badge >
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>本地IP</span>
                        </q-item-label>
                        <q-item-label>{{props.row.ip}}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>主机名</span>
                        </q-item-label>
                        <q-item-label>{{props.row.hostName}}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>操作系统</span>
                        </q-item-label>
                        <q-item-label>{{props.row.os}}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>版本</span>
                        </q-item-label>
                        <q-item-label>
                          <span class="vertical-middle">{{props.row.version}}</span>
                          <q-badge v-if="latestVersion && props.row.version !== latestVersion" class="vertical-middle q-ml-xs" color="warning">可升级</q-badge>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>令牌名称</span>
                        </q-item-label>
                        <q-item-label>{{props.row.accessTokenName || '-'}}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="flagDetail">
                      <q-item-section>
                        <q-item-label caption>
                          <span>令牌过期时间</span>
                        </q-item-label>
                        <q-item-label>
                          <template v-if="props.row.accessTokenEndTime">
                            <span class="vertical-middle">{{ dayjs(props.row.accessTokenEndTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            <q-badge v-if="props.row.accessTokenEndTime <= new Date().getTime()" class="vertical-middle q-ml-xs" color="warning">令牌过期</q-badge>
                            <q-badge v-else-if="props.row.accessTokenEndTime && props.row.accessTokenEndTime - new Date().getTime() < 30*24*60*60*1000" class="vertical-middle q-ml-xs" color="warning">即将过期</q-badge>
                          </template>
                          <template v-else>
                            -
                          </template>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </q-card>
              </div>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/agent-join.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何接入新节点？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.list-agent{
  &-table{
    .agent-card{

      .q-item {
        padding: 8px;
      }

      .title {
        font-size: 16px;
      }
    }
  }
}
</style>
  

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, watch, onMounted, onUnmounted } from 'vue'
import agentApi from '@/api/agent.api'

const columns = [
  { name: 'agentId', label: '节点ID', field: 'agentId', sortable: true },
  { name: 'active', label: '状态', field: 'active' },
  { name: 'ip', label: '本地IP', field: 'ip' },
  { name: 'hostName', label: '主机名', field: 'hostName' },
  { name: 'os', label: '操作系统', field: 'os' },
  { name: 'version', label: '版本', field: 'version' },
  { name: 'accessTokenName', label: '令牌名称', field: 'accessTokenName' },
  { name: 'accessTokenEndTime', label: '令牌过期时间', field: 'accessTokenEndTime' },
  { name: 'action' },
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const agentId = ref('')
    const agents = ref([])
    const loading = ref(false)
    const loadingVersion = ref(false)
    const latestVersion = ref(null)
    const releaseTime = ref(null)
    const flagDetail = ref(true)

    const pagination = ref({
      sortBy: 'agentId',
      descending: false,
      page: 1,
      rowsPerPage: 8,
      rowsNumber: 0
    })

    const getLatestVersion = () => {
      loadingVersion.value = true
      agentApi.getLatestVersion().then(resp => {
        latestVersion.value = resp.data.version
        releaseTime.value = resp.data.releaseTime

        loadingVersion.value = false
      })
    }

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
        agents.value = []
      }

      let fList = flagDetail.value ? agentApi.listDetail : agentApi.list
      fList({ 
        agentId: agentId.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
          agents.value = resp.data.page
          pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError(resp.message || '查询节点发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    let timer = null
    const startTimer = () => {
      const delay = 30*1000
      if(!timer){
        timer = setInterval(() => {
          search(true)
        }, delay)
      }
    }

    const stopTimer = () => {
      if(timer){
        clearInterval(timer)
      }
    }

    watch(flagDetail, () => {
      search()
    })

    onMounted(() => {
      search()
      getLatestVersion()
      startTimer()
    })

    onUnmounted(() => {
      stopTimer()
    })

    return {
      dayjs,

      agentId,
      agents,
      columns,
      filter: ref(''),
      pagination,
      loading,
      loadingVersion,
      latestVersion,
      releaseTime,
      flagDetail,

      onClickSearch(){
        pagination.value.page = 1
        search()
      },

      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        search(true)
      },

      onClickRestartAgent(agentId){
        agentApi.restart({ agentId }).then(resp => {
          for(let i = 0; i < agents.value.length; ++i){
            const agent = agents.value[i]
            if(agent.agentId == agentId){
              agent.flagRestarting = true
              break
            }
          }
          qUtil.notifySuccess('重启开始')
        }, resp => {
          qUtil.notifyError(resp.message || '重启发生错误')
        })
      },

      onClickConfigAgent(agentId){
        router.push(`/workspace/agent/${agentId}/config`)
      },

      onClickUpgradeAgent(agentId){
        agentApi.upgrade({ agentId }).then(resp => {
          for(let i = 0; i < agents.value.length; ++i){
            const agent = agents.value[i]
            if(agent.agentId == agentId){
              agent.flagUpgrading = true
              break
            }
          }
          qUtil.notifySuccess('升级开始')
        }, resp => {
          qUtil.notifyError(resp.message || '升级发生错误')
        })
      },

      onClickUninstallAgent(agentId){
        $q.dialog({
          title: '卸载',
          message: `确定卸载节点 ${agentId} ？如果节点不在线，将只删除节点记录，不卸载本地服务`,
          cancel: true,
        }).onOk(data => {
          agentApi.uninstall({ agentId }).then(resp => {
            search(true)
          }, resp => {
            qUtil.notifyError(resp.message || '卸载发生错误')
          })
        })
      },

      onClickCleanAgent(agentId){
        $q.dialog({
          title: '清理',
          message: `确定清理节点 ${agentId} ？缓存目录（cache）和工作目录（workspace）将被删除`,
          cancel: true,
        }).onOk(data => {
          qUtil.notifySuccess('清理开始')
          agentApi.clean({ agentId }).then(resp => {
            qUtil.notifySuccess('清理完成')
          }, resp => {
            qUtil.notifyError(resp.message || '清理发生错误')
          })
        })
      },
    }
  }
};
</script>