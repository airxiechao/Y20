<template>
  <LayoutTwoColumn class="monitor-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar v-if="$q.screen.gt.xs">
            <q-input dense outlined class="full-width" bg-color="white" v-model="name" placeholder="搜索监视" @change="onClickSearch">
              <template v-slot:before>
                <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" to="/workspace/project" />
              </template>
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="创建监视" @click="onClickCreateMonitor" />
              </template>
            </q-input>
          </q-toolbar>
          <q-toolbar v-else>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" to="/workspace/project" />
            <q-toolbar-title>
            </q-toolbar-title>
            <q-btn unelevated icon="add" color="primary" label="创建监视" @click="onClickCreateMonitor" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <div class="page-heading q-px-sm">
            <div class="row">
              <div class="self-center">监视</div>
            </div>
          </div>
          <q-table
            grid
            :hide-bottom="loading"
            :rows="monitors"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[8, 16, 32]"
            row-key="monitorId"
            :filter="filter"
            hide-header
            :loading="loading"
            @request="onTableSearch"
          >
            <template v-slot:no-data>
              <div class="full-width">
                <div class="row q-mt-xs">
                  <div class="col-12 col-md-6 col-lg-3">
                    <q-card class="monitor-card">
                      <q-item>
                        <q-item-section avatar>
                          <q-avatar icon="monitor" class="q-mr-sm text-grey" size="lg" style="background: #ECF2FF;" />
                        </q-item-section>

                        <q-item-section>
                          <q-item-label>
                            <span class="vertical-middle text-grey">没有监视？</span>
                            <q-btn class="q-mx-xs vertical-middle" color="primary" dense flat label="创建一个监视" @click="onClickCreateMonitor" />
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
                <div class="q-pa-sm col-12 col-md-6 col-lg-3" v-for="i in [1,2,3,4]" :key="i">
                  <q-card class="monitor-card">
                    <q-item>
                      <q-item-section avatar>
                        <q-skeleton type="QAvatar" animation="fade" size="25px" />
                      </q-item-section>

                      <q-item-section>
                        <q-item-label>
                          <q-skeleton type="text" animation="fade" />
                        </q-item-label>
                        <q-item-label caption>
                          <q-skeleton type="text" animation="fade" width="50%" />
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-card>
                </div>
              </div>
            </template>
            <template v-slot:item="props">
              <div class="q-pa-sm col-12 col-md-6 col-lg-3">
                <q-card class="monitor-card full-height">
                  <q-card-section horizontal class="row">
                    <q-list class="col">
                      <q-item>
                        <q-item-section avatar class="cursor-pointer" @click="onClickEditMonitor(props.row.monitorId)">
                          <q-icon v-if="props.row.status == 'OK'" color="green" size="sm" name="radio_button_unchecked" class="q-mr-sm"/>
                          <q-icon v-else-if="props.row.status == 'ERROR'" color="red" size="sm" name="priority_high" class="q-mr-sm"/>
                          <q-icon v-else color="grey" size="sm" name="priority_high" class="q-mr-sm"/>
                        </q-item-section>
                        <q-item-section class="title cursor-pointer" @click="onClickEditMonitor(props.row.monitorId)">
                          <q-item-label>
                            <span>{{ props.row.name }}</span>
                          </q-item-label>
                          <q-item-label caption>
                            <span class="vertical-middle q-pr-xs">{{ props.row.type }}</span>
                            <template v-if="props.row.lastUpdateTime">
                              <q-icon class="vertical-middle" name="update" />
                              <span class="vertical-middle" >{{ getTimeAgo(props.row.lastUpdateTime) }}</span>
                            </template>
                          </q-item-label>
                        </q-item-section>
                        <q-item-section side>
                          <q-btn color="grey" flat round icon="more_vert">
                            <q-menu anchor="bottom right" self="top right">
                              <q-list style="min-width: 100px">
                                <q-item clickable v-close-popup @click="onClickEditMonitor(props.row.monitorId)">
                                  <q-item-section>编辑</q-item-section>
                                </q-item>
                                <q-separator />
                                <q-item clickable v-close-popup @click="onClickDeleteMonitor(props.row.monitorId, props.row.name)">
                                  <q-item-section class="text-negative">删除</q-item-section>
                                </q-item>
                              </q-list>
                            </q-menu>
                          </q-btn>
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-card-section>
                </q-card>
              </div>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.monitor-list{
  .monitor-card{
    padding: 8px 0 8px 8px;

    .title{
      font-size: 16px;
    }
  }
}
</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted, onUnmounted } from 'vue'
import monitorApi from '@/api/monitor.api'

const columns = [
  { name: 'name', label: '名称', field: 'name', align: 'center', sortable: true },
  { name: 'status', label: '状态', field: 'status', align: 'center', sortable: true  },
  { name: 'lastUpdateTime', label: '最后更新时间', field: 'lastUpdateTime', align: 'center', sortable: true  },
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const projectName = inject('projectName')

    const route = inject('useRoute')()
    const { projectId } = route.params

    const loading = ref(false)
    const pagination = ref({
      sortBy: 'createTime',
      descending: false,
      page: 1,
      rowsPerPage: 16,
      rowsNumber: 0,
    })

    const name = ref('')
    const monitors = ref([])

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
        monitors.value = []
      }
      monitorApi.list({ 
        name: name.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        monitors.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError(resp.message || '查询监视发生错误')
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

    onMounted(() => {
      store.commit('setTitle', { title: projectName.value })
      search()
      startTimer()
    })

    onUnmounted(() => {
      stopTimer()
    })

    return {
      dayjs,

      projectName,
      name,
      monitors,
      loading,

      columns,
      filter: ref(''),
      pagination,

      getTimeAgo(time){
        const dd = dayjs().diff(dayjs(time), 'day')
        if(dd > 0){
          if(dd > 90){
            return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
          }else{
            return `${dd}天前`
          }
        }

        const hh = dayjs().diff(dayjs(time), 'hour')
        if(hh > 0){
          return `${hh}小时前`
        }

        const mm = dayjs().diff(dayjs(time), 'minute')
        if(mm > 0){
          return `${mm}分钟前`
        }

        const ss = dayjs().diff(dayjs(time), 'second')
        if(ss >= 0){
          return `${ss}秒前`
        }
      },

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

      onClickCreateMonitor(){
        router.push(`/project/${projectId}/monitor/create`)
      },

      onClickEditMonitor(monitorId){
        router.push(`/project/${projectId}/monitor/${monitorId}`)
      },

      onClickDeleteMonitor(monitorId, monitorName){
        $q.dialog({
          title: '删除',
          message: `确定删除监视 ${monitorName} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          monitorApi.delete({ monitorId }).then(resp => {
            search()
            qUtil.notifySuccess('删除完成')
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

    }
  }
};
</script>