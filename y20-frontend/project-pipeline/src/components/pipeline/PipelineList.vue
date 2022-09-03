<template>
  <LayoutTwoColumn class="pipeline-list">
    <template v-slot:center>
      <q-dialog v-model="flagShowCopyDialog" persistent>
        <q-card style="min-width: 350px">
          <q-card-section class="q-pb-none">
            <div class="text-h6">复制到</div>
          </q-card-section>

          <q-card-section>
            <q-form
              ref="copyForm"
              @submit="onSubmitCopy"
              class="q-gutter-md"
            >
              <q-select
                outlined
                v-model="copyToProjectId"
                :options="projects" 
                :label="`${$t('label-project')} *`"
                :hint="$t('error-no-project')"
                emit-value 
                map-options 
                @filter="onSelectProject"
                lazy-rules
                :rules="[ 
                    val => val !== null || $t('error-no-project'),
                ]"
              />

              <q-input
                outlined
                v-model="copyToPipelineName"
                :label="`${$t('label-name')} *`"
                :hint="$t('error-name-too-long')"
                lazy-rules
                :rules="[ 
                    val => val && val.length > 0 || $t('error-no-name'),
                    val => val.length < 100 || $t('error-name-too-long')
                ]"
              />
            </q-form>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat color="primary" label="取消" v-close-popup />
            <q-btn flat color="primary" label="确定" :loading="copyLoading" @click="copyForm.submit()"/>
          </q-card-actions>
        </q-card>
      </q-dialog>
      
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar v-if="$q.screen.gt.xs">
            <q-input dense outlined class="full-width" bg-color="white" v-model="name" placeholder="搜索流水线" @change="onClickSearch">
              <template v-slot:before>
                <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" to="/workspace/project" />
              </template>
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="创建流水线" @click="onClickCreatePipeline" />
              </template>
            </q-input>
          </q-toolbar>
          <q-toolbar v-else>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" to="/workspace/project" />
            <q-toolbar-title>
            </q-toolbar-title>
            <q-btn unelevated icon="add" color="primary" label="创建流水线" @click="onClickCreatePipeline" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <div class="page-heading q-px-sm">
            <div class="row">
              <div class="self-center">流水线</div>
              <q-space />
              <div class="self-center">
                <q-toggle class="text-subtitle2" size="sm" v-model="flagDetail" label="详细" />
              </div>
            </div>
          </div>
          <q-table
            grid
            :hide-bottom="loading"
            :rows="pipelines"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[8, 16, 32]"
            row-key="id"
            :filter="filter"
            hide-header
            :loading="loading"
            @request="onTableSearch"
          >
            <template v-slot:no-data>
              <div class="full-width">
                <div class="row q-mt-xs">
                  <div class="col-12 col-md-6 col-lg-3">
                    <q-card class="pipeline-card">
                      <q-item>
                        <q-item-section avatar>
                          <q-avatar icon="play_arrow" class="q-mr-sm text-grey" size="lg" style="background: #ECF2FF;" />
                        </q-item-section>

                        <q-item-section>
                          <q-item-label>
                            <span class="vertical-middle text-grey">没有流水线？</span>
                            <q-btn class="q-mx-xs vertical-middle" color="primary" dense flat label="创建一条流水线" @click="onClickCreatePipeline" />
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
                  <q-card class="pipeline-card">
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

                    <q-card-section v-for="j in flagDetail ? [1,2] : [1]" :key="j">
                      <q-skeleton type="text" width="50%" class="text-subtitle2" animation="fade" />
                      <q-skeleton type="text" class="text-subtitle2" animation="fade" />
                    </q-card-section>
                  </q-card>
                </div>
              </div>
            </template>
            <template v-slot:item="props">
              <div class="q-pa-sm col-12 col-md-6 col-lg-3">
                <q-card class="pipeline-card">
                  <q-card-section horizontal class="row">
                    <q-list class="col">
                      <q-item>
                        <!-- <q-item-section avatar class="cursor-pointer" @click="onClickDetailPipeline(props.row.id)">
                          <q-icon size="md" name="assignment" class="q-mr-sm"/>
                        </q-item-section> -->
                        <q-item-section>
                          <q-item-label :lines="1" class="title cursor-pointer" style="word-break: break-all;" @click="onClickDetailPipeline(props.row.id)">
                            <span>
                              {{ props.row.name }}
                              <!-- <q-tooltip anchor="top left" self="bottom left" :offset="[0, 8]">{{ props.row.name }}</q-tooltip> -->
                            </span>
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                      
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>最近执行状态</q-item-label>
                          <q-item-label>
                            <span v-if="props.row.lastStatus" :class="{
                              'cursor-pointer': true,
                              'text-orange': ['STARTED', 'RUNNING', 'WAITING'].includes(props.row.lastStatus),
                              'text-green': props.row.lastStatus == 'PASSED',
                              'text-red': props.row.lastStatus == 'FAILED',
                            }" @click="onClickDetailPipelineRun(props.row.id, props.row.lastPipelineRunId)">{{ props.row.lastStatus }}</span>
                            <span v-else>-</span>
                          </q-item-label>
                        </q-item-section>
                      </q-item>

                      <q-item v-if="flagDetail">
                        <q-item-section>
                          <q-item-label caption>
                            <span>最近启动时间</span>
                          </q-item-label>
                          <q-item-label>
                            <span v-if="props.row.lastBeginTime">{{ dayjs(props.row.lastBeginTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                            <span v-else>-</span>
                          </q-item-label>
                        </q-item-section>
                      </q-item>

                      <q-item v-if="flagDetail">
                        <q-item-section>
                          <q-item-label caption>
                            <span>最近持续时间</span>
                          </q-item-label>
                          <q-item-label>
                            <span v-if="props.row.lastBeginTime">{{ dayjs.duration((props.row.lastEndTime || Date.now()) - props.row.lastBeginTime).format('HH:mm:ss') }}</span>
                            <span v-else>-</span>
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                    <q-card-actions vertical class="actions justify-start">
                      <q-btn class="text-primary" style="background: #ECF2FF;" flat round size="sm" icon="play_arrow" @click="onClickCreatePipelineRun(props.row.id)" />
                      <q-btn v-if="flagDetail" class="text-grey" flat round size="sm" icon="history" @click="onClickListPipelineRun(props.row.id)" />
                      <q-btn v-if="flagDetail" class="text-grey" flat round size="sm" icon="account_tree" @click="onClickEditPipelineStep(props.row.id)" />
                      <q-btn v-if="flagDetail" class="text-grey" flat round size="sm" icon="superscript" @click="onClickEditPipelineVariable(props.row.id)" />
                      <q-btn class="text-grey" flat round size="sm" icon="more_horiz">
                        <q-menu anchor="bottom right" self="top right">
                          <q-list>
                            <q-item clickable v-close-popup @click="onClickCreatePipelineRun(props.row.id)">
                              <q-item-section>启动</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickPublishPipeline(props.row.id)">
                              <q-item-section>发布应用</q-item-section>
                            </q-item>
                            <q-separator />
                            <q-item clickable v-close-popup @click="onClickListPipelineRun(props.row.id)">
                              <q-item-section>执行历史</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelineStep(props.row.id)">
                              <q-item-section>步骤</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelineVariable(props.row.id)">
                              <q-item-section>变量</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelineFile(props.row.id)">
                              <q-item-section>文件</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelineTrigger(props.row.id)">
                              <q-item-section>触发</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelinePending(props.row.id)">
                              <q-item-section>队列</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickEditPipelineConfig(props.row.id)">
                              <q-item-section>设置</q-item-section>
                            </q-item>
                            <q-separator />
                            <q-item clickable v-close-popup @click="onClickCopyPipeline(props.row.id, props.row.name)">
                              <q-item-section>复制</q-item-section>
                            </q-item>
                            <q-item clickable v-close-popup @click="onClickDeletePipeline(props.row.id, props.row.name)">
                              <q-item-section class="text-negative">删除</q-item-section>
                            </q-item>
                          </q-list>
                        </q-menu>
                      </q-btn>
                    </q-card-actions>
                  </q-card-section>
                  <div class="tag bg-white">
                    <q-icon
                      class="cursor-pointer"
                      :name="props.row.bookmarked ? 'bookmark' : 'bookmark_border'"
                      color="primary" 
                      @click="onClickUpdateBookmark(props.row.id, !props.row.bookmarked)"
                    >
                      <q-tooltip>书签</q-tooltip>
                    </q-icon>
                    <q-icon name="timer" color="green" v-if="props.row.flagCronEnabled">
                      <q-tooltip>定时</q-tooltip>
                    </q-icon>
                  </div>
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
          <q-item clickable tag="a" href="/docs/guide/pipeline-edit.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何编排流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

          <q-item clickable tag="a" href="/docs/guide/pipeline-run.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何运行流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

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
.pipeline-list{
  .pipeline-card{
    padding: 8px 0 8px 8px;

    .title{
      font-size: 16px;
    }

    .tag{
      position: absolute;
      line-height: 14px;
      top: -5px;
      left: 3px;
    }

    .actions{
      border-left: 1px dashed #ddd;
    }
  }
}
</style>

<i18n>
{
  "zhCN": {
    "label-project": "项目",
    "error-no-project": "请选择目的项目",
    "label-name": "名称",
    "error-no-name": "请输入流水线名称",
    "error-name-too-long": "名称长度不超过100个字符",
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted, onUnmounted } from 'vue'
import pipelineApi from '@/api/pipeline.api'
import projectApi from '@/api/project.api'

const columns = [
  { name: 'name', label: '名称', field: 'name', align: 'center', sortable: true },
  { name: 'lastStatus', label: '最近执行状态', field: 'status', align: 'center', sortable: true  },
  { name: 'lastBeginTime', label: '最近启动时间', field: 'lastBeginTime', align: 'center', sortable: true  },
  { name: 'elapsedTime', label: '最近持续时间', field: '', align: 'center', sortable: false },
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
      sortBy: 'bookmarked,bookmarkTime',
      descending: true,
      page: 1,
      rowsPerPage: 16,
      rowsNumber: 0,
    })

    const name = ref('')
    const pipelines = ref([])
    const projects = ref([])
    const copyPipelineId = ref(null)
    const copyToProjectId = ref(null)
    const copyToPipelineName = ref(null)
    const flagShowCopyDialog = ref(false)
    const copyForm = ref(null)
    const copyLoading = ref(false)
    const flagDetail = ref(false)

    const searchProject = (inputValue, doneFn, abortFn) => {
      projectApi.list({ 
        name: inputValue
       }).then(resp => {
        projects.value = resp.data.page.map(a => {
          return {
            label: a.name,
            value: a.projectId,
          }
        })

        if(doneFn){
          doneFn()
        }
      }, resp => {
        if(abortFn){
          abortFn()
        }
      })
    }

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
        pipelines.value = []
      }
      pipelineApi.list({ 
        name: name.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        pipelines.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError(resp.message || '查询流水线发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    let timer = null
    const startTimer = () => {
      const delay = 10*1000
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
      pipelines,
      projects,
      copyPipelineId,
      copyToProjectId,
      copyToPipelineName,

      columns,
      filter: ref(''),
      pagination,

      loading,
      copyForm,
      copyLoading,
      flagShowCopyDialog,
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

      onSelectProject(inputValue, doneFn, abortFn){
        searchProject(inputValue, doneFn, abortFn)
      },

      onClickCreatePipeline(){
        router.push(`/project/${projectId}/pipeline/create`)
      },

      onClickDetailPipeline(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}`)
      },

      onClickDetailPipelineRun(pipelineId, pipelineRunId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run/${pipelineRunId}`)
      },

      onClickCreatePipelineRun(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run/create`)
      },

      onClickListPipelineRun(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run`)
      },

      onClickPublishPipeline(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/publish`)
      },

      onClickEditPipelineStep(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
      },

      onClickEditPipelineVariable(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/variable`)
      },

      onClickEditPipelineFile(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/file`)
      },

      onClickEditPipelineTrigger(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/trigger`)
      },

      onClickEditPipelinePending(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/pending`)
      },

      onClickEditPipelineConfig(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/config`)
      },

      onClickCreatePipelineRun(pipelineId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run/create`)
      },

      onClickCopyPipeline(pipelineId, pipelineName){
        flagShowCopyDialog.value = true
        copyPipelineId.value = pipelineId
        copyToPipelineName.value = pipelineName + '-复制'
        copyToProjectId.value = null
      },

      onClickDeletePipeline(pipelineId, pipelineName){
        $q.dialog({
          title: '删除',
          message: `确定删除流水线 ${pipelineName} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          pipelineApi.delete({ pipelineId }).then(resp => {
            search()
            qUtil.notifySuccess('删除完成')
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

      onSubmitCopy(){
        copyLoading.value = true
        pipelineApi.copy({ 
          pipelineId: copyPipelineId.value,
          toProjectId: copyToProjectId.value,
          toPipelineName: copyToPipelineName.value,
        }).then(resp => {
          search()
          flagShowCopyDialog.value = false
          qUtil.notifySuccess('复制完成')
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(() => {
          copyLoading.value = false
        })
      },

      onClickUpdateBookmark(pipelineId, bookmarked){
        pipelineApi.updateBookmark({
          pipelineId,
          bookmarked,
        }).then(resp => {
          search()
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      },

    }
  }
};
</script>