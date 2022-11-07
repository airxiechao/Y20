<template>
  <LayoutTwoColumn class="project-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-input class="full-width" dense outlined bg-color="white" placeholder="搜索项目" v-model="name" @change="onClickSearch">
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="创建项目" @click="onClickCreateProject" />
              </template>
            </q-input>
          </q-toolbar>
        </div>
        <div class="q-pa-xs page-content">
          
          <NewbieGuide v-if="flagGuide" @stepTo="onGuideStepTo" @close="onGuideClose"/>

          <q-table
            grid
            :hide-bottom="projectLoading"
            :rows="projects"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[8, 16, 32]"
            row-key="projectId"
            :filter="filter"
            hide-pagination
            hide-header
            @request="onTableSearch"
            :loading="projectLoading"
          >
            <template v-slot:no-data>
              <div class="full-width">
                <div class="row q-mt-xs">
                  <div class="col-12 col-md-6 col-lg-3">
                    <q-card flat class="project-card q-pa-xs">
                      <q-item>
                        <q-item-section avatar>
                          <q-avatar icon="bar_chart" class="q-mr-sm text-grey" size="lg" style="background: #ECF2FF;" />
                        </q-item-section>

                        <q-item-section>
                          <q-item-label>
                            <span class="vertical-middle text-grey">没有项目？</span>
                            <q-btn class="q-mx-xs vertical-middle" color="primary" dense flat label="创建一个项目" @click="onClickCreateProject" />
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
                  <q-card flat class="project-card q-pa-xs">
                    <q-item>
                      <q-item-section avatar>
                        <q-skeleton type="QAvatar" animation="fade" size="38px" />
                      </q-item-section>

                      <q-item-section>
                        <q-item-label>
                          <q-skeleton type="text" animation="fade" />
                        </q-item-label>
                        <q-item-label caption>
                          <q-skeleton type="text" animation="fade" />
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-card>
                </div>
              </div>
            </template>
            <template v-slot:item="props">
              <div class="q-pa-xs col-12 col-md-6 col-lg-3">
                <q-card flat class="project-card q-pa-xs" >
                  <q-item>
                    <q-item-section avatar class="cursor-pointer" @click="onClickProject(props.row.projectId)">
                      <q-avatar icon="bar_chart" class="q-mr-sm text-primary" size="lg" style="background: #ECF2FF;" />
                    </q-item-section>
                    <q-item-section class="title cursor-pointer" @click="onClickProject(props.row.projectId)">
                      <q-item-label :lines="1">
                        {{ props.row.name }}
                      </q-item-label>
                      <q-item-label caption :lines="1">
                        <span class="vertical-middle">{{props.row.numPipeline !== undefined ? props.row.numPipeline : '?'}} 流水线</span>
                        <span v-if="props.row.numRunning">
                          <q-spinner-facebook class="vertical-middle q-ml-xs" color="orange"></q-spinner-facebook>
                          <q-tooltip>流水线运行中</q-tooltip>
                        </span>
                      </q-item-label>
                      <!-- <q-tooltip anchor="top left" self="bottom left" :offset="[0, 8]">{{ props.row.name }}</q-tooltip> -->
                    </q-item-section>
                    <q-item-section side>
                      <q-btn color="grey" flat round icon="more_vert">
                        <q-menu anchor="bottom right" self="top right">
                          <q-list style="min-width: 100px">
                            <q-item clickable v-close-popup @click="onClickEditProject(props.row.projectId)">
                              <q-item-section>重命名</q-item-section>
                            </q-item>
                            <q-separator />
                            <q-item clickable v-close-popup @click="onClickDeleteProject(props.row.projectId, props.row.name)">
                              <q-item-section class="text-negative">删除</q-item-section>
                            </q-item>
                          </q-list>
                        </q-menu>
                      </q-btn>
                    </q-item-section>
                  </q-item>

                  <q-icon 
                    class="bookmark cursor-pointer bg-white" 
                    :name="props.row.bookmarked ? 'bookmark' : 'bookmark_border'" 
                    color="primary"
                    @click="onClickUpdateBookmark(props.row.projectId, !props.row.bookmarked)"
                  >
                    <q-tooltip>书签</q-tooltip>
                  </q-icon>
                </q-card>
              </div>
            </template>
          </q-table>

          <q-card flat class="q-mx-xs q-mt-xs">
            <div class="page-heading q-px-md q-pt-xs">
              <span class="vertical-middle">项目动态</span>
            </div>
            <div class="relative-position">
              <template v-if="activityLoading">
                <q-item v-for="ai in [1]" :key="ai" style="max-width: 700px;" class="q-pb-md">
                  <q-item-section>
                    <q-item-label caption>
                      <q-skeleton type="text" animation="fade" style="width: 25%;"/>
                    </q-item-label>
                    <q-item-label>
                      <q-skeleton type="text" animation="fade" />
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </template>
              <template v-else>
                <div class="q-pa-md q-mt-xs text-grey" v-if="activities.length == 0" style="font-size: 12px;">
                  没有什么动静
                </div>
                <q-card flat v-if="activities.length > 0 || activityPageNo > 1" class="activity-card q-pb-md q-pl-md q-pt-sm q-pr-sm" >
                  <q-scroll-area style="height: 305px;" :visible="true">
                    <q-infinite-scroll @load="onLoadActivity" :offset="100">
                      <template v-for="(activity, i) in activities" :key="i">
                        <div class="activity-card-entry q-mb-md q-pr-sm">
                          <div class="text-grey">
                            <span class="vertical-middle" >
                              {{getTimeAgo(activity.time)}}
                            </span>
                            <span class="q-ml-sm">
                              <q-icon class="vertical-middle q-mr-xs" name="timer" />
                              <span class="vertical-middle" >
                                {{ dayjs.duration(activity.event.endTime - activity.event.beginTime).format('HH:mm:ss') }}
                              </span>
                            </span>
                          </div>
                          <div class="q-mt-xs row rounded-borders">
                            <div class="col-12 col-sm-6">
                              流水线：
                              <router-link :to="`/project/${activity.event.projectId}/pipeline/${activity.event.pipelineId}`">
                                {{activity.event.pipelineName}}
                              </router-link>
                            </div>
                            <div class="col-12 col-sm-6">
                              执行：
                              <router-link class="q-mr-sm" :to="`/project/${activity.event.projectId}/pipeline/${activity.event.pipelineId}/run/${activity.event.pipelineRunId}`">
                                {{activity.event.pipelineRunName}}
                              </router-link>
                              <q-icon v-if="activity.event.status == 'PASSED'" name="check" color="green" size="xs" />
                              <q-icon v-else-if="activity.event.status == 'FAILED'" name="close" color="red" size="xs" />
                            </div>
                          </div>
                        </div>
                      </template>
                      <template v-slot:loading>
                        <q-item v-for="ai in [1]" :key="ai" class="q-mt-sm" style="max-width: 700px; padding-left: 0;">
                          <q-item-section>
                            <q-item-label caption>
                              <q-skeleton type="text" animation="fade" style="width: 25%;"/>
                            </q-item-label>
                            <q-item-label>
                              <q-skeleton type="text" animation="fade" />
                            </q-item-label>
                          </q-item-section>
                        </q-item>
                      </template>
                    </q-infinite-scroll>
                  </q-scroll-area>
                </q-card>
              </template>
              <!-- <q-inner-loading :showing="activityLoading">
                <q-spinner-gears size="30px" color="primary" />
              </q-inner-loading> -->
            </div>
          </q-card>

          <q-card flat class="q-mx-xs q-mt-sm q-mb-xs">
            <div class="page-heading q-px-md q-pt-xs">
              <span class="vertical-middle">监视状态</span>
            </div>
            <div class="relative-position q-pb-xs">
              <template v-if="monitorLoading">
                <div class="row">
                  <div class="q-px-md q-py-sm col-12 col-md-6 col-lg-3" v-for="ai in [1,2,3,4]" :key="ai">
                    <q-skeleton type="text" animation="fade" />
                  </div>
                </div>
              </template>
              <template v-else>
                <div class="q-pa-md q-mt-xs text-grey" v-if="monitors.length == 0" style="font-size: 12px;">
                  没有设置监视
                </div>
                <div class="row">
                  <div class="q-px-md q-py-sm col-12 col-md-6 col-lg-3" v-for="monitor in monitors" :key="monitor.monitorId">
                    <q-chip v-if="monitor.status == 'OK'" square>
                      <q-avatar icon="radio_button_unchecked" color="green" text-color="white" />
                      <span class="q-pl-xs">{{monitor.name}}</span>
                    </q-chip>
                    <q-chip v-else-if="monitor.status == 'ERROR'" square>
                      <q-avatar icon="priority_high" color="red" text-color="white" />
                      <span class="q-pl-xs">{{monitor.name}}</span>
                    </q-chip>
                    <q-chip v-else square>
                      <q-avatar icon="priority_high" color="grey" text-color="white" />
                      <span class="q-pl-xs">{{monitor.name}}</span>
                    </q-chip>
                  </div>
                </div>
              </template>
              <!-- <q-inner-loading :showing="activityLoading">
                <q-spinner-gears size="30px" color="primary" />
              </q-inner-loading> -->
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/pipeline-edit.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何编排流水线？</q-item-section>
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
.project-list{
  .project-card{

    .title{
      font-size: 16px;
      word-break: break-all;
    }

    .bookmark{
      position: absolute;
      top: -5px;
      left: 3px;
    }

    .q-item {
      padding: 8px;
    }
  }

  .activity-card{
    // background: transparent !important;

    &-entry{
      max-width: 650px;
      word-break: break-all;
      border-bottom: 1px solid #eee;
      padding-bottom: 8px;
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
import NewbieGuide from '@/components/project/NewbieGuide'
import { ref, inject, watch, onMounted, onUnmounted } from 'vue'
import projectApi from '@/api/project.api'
import activityApi from '@/api/activity.api'
import monitorApi from '@/api/monitor.api'

const columns = [
  { name: 'name', label: '名称', field: 'name' },
]

export default {
  components: {
    LayoutTwoColumn,
    NewbieGuide,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const flagGuide = ref(false)
    const name = ref('')

    const projects = ref([])
    const projectLoading = ref(false)
    
    const activities = ref([])
    const activityLoading = ref(false)
    const activityPageNo = ref(1)
    const activityPageSize = ref(5)

    const monitors = ref([])
    const monitorLoading = ref(false)

    const pagination = ref({
      sortBy: 'bookmarked,bookmarkTime',
      descending: true,
      page: 1,
      rowsPerPage: 100,
      rowsNumber: 0
    })

    const searchProject = (noLoading) => {
      if(!noLoading){
        projectLoading.value = true
        projects.value = []
      }
      projectApi.listDetail({ 
        name: name.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        projects.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError(resp.message || "查询项目发生错误")
      }).finally(() => {
        projectLoading.value = false
      })
    }

    const listLastActivity = () => {
      return activityApi.list({ 
        pageNo: 1, 
        pageSize: activityPageSize.value,
      }).then(resp => {
        const data = resp.data.page
        if(activities.value.length == 0){
          activities.value.push(...data)
        }else{
          const top = activities.value[0]
          for(let i = data.length - 1; i >=0; --i){
            const activity = data[i]
            if(activity.time > top.time){
              activities.value.unshift(activity)
            }
          }
        }
      }, resp => {
        //qUtil.notifyError(resp.message || "查询最新项目动态发生错误")
      })
    }

    const listActivity = (done) => {
      return activityApi.list({ 
        pageNo: activityPageNo.value, 
        pageSize: activityPageSize.value,
      }).then(resp => {
        const data = resp.data.page
        activities.value.push(...data)
        if(activities.value.length == 0 && activityPageNo.value == 1){
          flagGuide.value = true
        }

        if(done){
          done(data.length < activityPageSize.value)
        }
      }, resp => {
        //qUtil.notifyError(resp.message || "查询项目动态发生错误")
      })
    }

    const listMonitor = (noLoading) => {
      if(!noLoading){
        monitorLoading.value = true
        monitors.value = []
      }

      monitorApi.list().then(resp => {
        monitors.value = resp.data.page
      }, resp => {
        //qUtil.notifyError(resp.message || "查询监视发生错误")
      }).finally(() => {
        monitorLoading.value = false
      })
    }

    let timer = null
    const startTimer = () => {
      const delay = 10*1000
      if(!timer){
        timer = setInterval(() => {
          searchProject(true)
          listLastActivity()
          listMonitor(true)
        }, delay)
      }
    }

    const stopTimer = () => {
      if(timer){
        clearInterval(timer)
      }
    }

    onMounted(() => {
      store.commit('setTitle', { title: '项目' })
      store.commit('project/setProjectId', { id: null })
      store.commit('project/setProjectName', { name: null})

      searchProject()
      startTimer()
      
      activityLoading.value = true
      listActivity().finally(() => {
        activityLoading.value = false
      })

      listMonitor()
    })

    onUnmounted(() => {
      stopTimer()
    })

    return {
      dayjs,
      $q,

      flagGuide,
      name,
      projects,
      columns,
      filter: ref(''),
      pagination,
      projectLoading,
      activities,
      activityLoading,
      activityPageNo,
      activityPageSize,
      monitors,
      monitorLoading,

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
        searchProject()
      },
      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        searchProject(true)
      },
      onClickCreateProject(){
        router.push('/workspace/project/create')
      },
      onClickProject(projectId){
        router.push(`/project/${projectId}/pipeline`)
      },

      onClickEditProject(projectId){
        router.push(`/workspace/project/${projectId}`)
      },

      onClickDeleteProject(projectId, projectName){
        $q.dialog({
          title: '删除',
          message: `确定删除项目 ${projectName} ？`,
          cancel: true,
        }).onOk(data => {
          projectApi.delete({ projectId }).then(resp => {
            searchProject()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

      onClickUpdateBookmark(projectId, bookmarked){
        projectApi.updateBookmark({
          projectId,
          bookmarked: bookmarked,
        }).then(resp => {
          searchProject(true)
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      },

      onGuideStepTo(step){
        switch(step){
          case 2:
            searchProject(true)
            break;
        }
      },

      onGuideClose(){
        flagGuide.value = false
      },

      onLoadActivity(index, done){
        activityPageNo.value += 1
        listActivity(done)
      },
    
    }
  }
};
</script>