<template>
  <LayoutTwoColumn class="pipeline-run-detial-step">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="返回" :to="`/project/${projectId}/pipeline/${pipelineId}`" />
            <q-toolbar-title>
            </q-toolbar-title>
            <q-btn flat label="重启" color="primary" :to="`/project/${projectId}/pipeline/${pipelineId}/run/create`" />
            <q-btn flat label="编辑" color="primary" :to="`/project/${projectId}/pipeline/${pipelineId}/step`" />
            <q-btn
              v-if="pipelineRun.status && !['PASSED', 'FAILED'].includes(pipelineRun.status)"
              flat
              color="red" 
              icon="stop"
              label="停止"
              :loading="stopLoading"
              :disable="['PASSED', 'FAILED'].includes(pipelineRun.status)"
              @click="onClickStopPipelineRun"
            />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content relative-position">
          <div class="q-mb-sm">
            <q-bar dark class="rounded-borders">
              <div class="page-heading">执行情况</div>
              <q-toggle size="xs" color="primary" style="font-size: 14px;" v-model="flagDetail" label="详细" />
              <q-space />
              <q-btn 
                flat 
                dense 
                icon="folder" 
                :label="$q.screen.gt.xs?'上传的文件':undefined"
                :to="`/project/${projectId}/pipeline/${pipelineId}/run/${pipelineRunId}/file`" 
              />
            </q-bar>
          </div>
          <q-card flat v-if="loading" class="q-pa-md">
            <q-skeleton type="text" animation="fade" style="max-width: 200px;" />
            <div class="row" v-for="pi in [1,2,3]" :key="pi">
              <div class="q-pr-sm" style="width: 80px;"><q-skeleton type="text" animation="fade" /></div>
              <div class="col"><q-skeleton type="text" animation="fade" /></div>
            </div>
          </q-card>
          <q-card flat v-else class="pipeline-run-detial-step-top q-px-md q-pt-sm q-pb-xs">
            <div class="page-heading">
              <span>{{pipelineRun.name}}</span>
              <a :href="`/nav${fromUrl}`" target="_black"><q-icon v-if="fromUrl" name="link" class="cursor-pointer" /></a>
            </div>
            <div class="row">
              <div class="title">流水线</div>
              <div class="col"><router-link :to="`/project/${projectId}/pipeline/${pipelineId}`">{{pipelineName}}</router-link></div>
            </div>
            <div class="row" v-if="flagDetail && pipelineRun.beginTime">
              <div class="title">启动时间</div>
              <div class="col">{{ dayjs(pipelineRun.beginTime).format('YYYY-MM-DD HH:mm:ss') }}</div>
            </div>
            <div class="row" v-if="flagDetail &&pipelineRun.endTime">
              <div class="title">结束时间</div>
              <div class="col">{{ dayjs(pipelineRun.endTime).format('YYYY-MM-DD HH:mm:ss') }}</div>
            </div>
            <div class="row" v-if="pipelineRun.beginTime">
              <div class="title">持续时间</div>
              <div class="col">{{ dayjs.duration(pipelineRun.endTimeOrNow - pipelineRun.beginTime).format('HH:mm:ss') }}</div>
            </div>
            <div class="row">
              <div class="title">执行状态</div>
              <div class="col">
                <span :class="{
                  'text-orange': ['STARTED', 'RUNNING', 'WAITING'].includes(pipelineRun.status),
                  'text-green': pipelineRun.status == 'PASSED',
                  'text-red': pipelineRun.status == 'FAILED',
                }">
                  <template v-if="pipelineRun.status == 'RUNNING'">
                    <q-spinner-facebook class="vertical-middle q-mr-xs" color="orange" />
                  </template>
                  <span class="vertical-middle">{{ pipelineRun.status }}</span>
                  <template v-if="pipelineRun.status == 'FAILED'">
                    <span>
                      <q-icon name="error" class="vertical-top q-ml-xs" />
                      <q-tooltip class="bg-red">
                        {{ pipelineRun.error }}
                      </q-tooltip>
                    </span>
                  </template>
                  <template v-if="pipelineRun.status == 'RUNNING'">
                    <span class="vertical-middle q-ml-xs">{{ runningStepPos+1 }} / {{ pipelineStepRuns.length }}</span>
                  </template>
                </span>
              </div>
            </div>
            <div class="row" v-if="flagDetail">
              <div class="title self-center">调试模式</div>
              <div class="col"><q-toggle size="xs" dense :model-value="pipelineRun.flagDebug" /></div>
            </div>
          </q-card>

          <q-card flat v-if="flagDetail" class="q-my-sm">
            <q-list dense separator>
              <q-item-label header class="page-heading">输入变量</q-item-label>
              
              <q-markup-table v-if="loading" flat>
                <tbody>
                  <tr v-for="vi in 1" :key="vi">
                    <td>
                      <q-skeleton animation="fade" type="text" />
                    </td>
                    <td>
                      <q-skeleton animation="fade" type="text" />
                    </td>
                  </tr>
                </tbody>
              </q-markup-table>
              <template v-else-if="pipelineRun.inParams && Object.keys(pipelineRun.inParams).length > 0">
                <q-item v-for="(value, name) in pipelineRun.inParams" :key="name">
                  <q-item-section>
                    <q-item-label style="word-break: break-all;">{{name}}</q-item-label>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-grey-7" style="word-break: break-all;">{{value}}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
              <q-item v-else>
                <q-item-section>
                  <q-item-label class="text-grey-7">无</q-item-label>
                </q-item-section>
              </q-item>

              <template v-if="['PASSED', 'FAILED'].includes(pipelineRun.status)">
                <q-separator />
                <q-item-label header class="page-heading">输出变量</q-item-label>
                <template v-if="pipelineRun.outParams && Object.keys(pipelineRun.outParams).length > 0">
                  <q-item v-for="(value, name) in pipelineRun.outParams" :key="name">
                    <q-item-section>
                      <q-item-label style="word-break: break-all;">{{name}}</q-item-label>
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-grey-7" style="word-break: break-all;">{{value}}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
                <q-item v-else>
                  <q-item-section>
                    <q-item-label class="text-grey-7">无</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-list>
          </q-card>

          <div class="q-my-sm">
            <q-bar dark class="rounded-borders">
              <div>执行步骤</div>
              <q-toggle size="xs" color="primary" style="font-size: 14px;" v-model="flagTail" label="跟随" />
              <q-space />
              <q-btn 
                :disabled="['PASSED', 'FAILED'].includes(pipelineRun.status)"
                dense flat
                icon="stop"
                :label="$q.screen.gt.xs?'停止':undefined"
                :loading="stopLoading"
                @click="onClickStopPipelineRun" >
              </q-btn>
              <q-btn
                :disabled="!(pipelineRun.status === 'WAITING' && waitingStepPos < pipelineStepRuns.length)"
                dense flat
                icon="play_arrow"
                :label="$q.screen.gt.xs?'下一步':undefined"
                @click="onClickForwardRun" />
              <q-btn
                :disabled="!(pipelineRun.status === 'WAITING' && waitingStepPos === pipelineStepRuns.length)"
                dense flat
                icon="skip_next"
                :label="$q.screen.gt.xs?'结束':undefined"
                @click="onClickForwardRun" />
            </q-bar>
          </div>
          <template v-if="loading">
            <q-card flat class="q-my-sm q-pa-sm" v-for="si in [1]" :key="si">
              <q-item>
                <q-item-section avatar>
                  <q-skeleton type="QAvatar" animation="fade" size="38px" />
                </q-item-section>

                <q-item-section>
                  <q-item-label>
                    <q-skeleton type="text" animation="fade" />
                  </q-item-label>
                  <q-item-label caption>
                    <q-skeleton type="text" animation="fade" style="width: 25%;" />
                  </q-item-label>
                </q-item-section>
              </q-item>
            </q-card>
          </template>
          <template v-else-if="pipelineStepRuns.length == 0">
            <q-card flat class="q-my-sm q-pa-md text-grey-7">
              无执行步骤
            </q-card>
          </template>
          <template v-else>
            <q-card flat class="q-my-sm">
              <q-card v-for="(stepRun, i) in pipelineStepRuns" :key="stepRun.id" >
                <q-list class="q-pt-xs">
                  <q-item>
                    <q-item-section v-if="$q.screen.gt.xs || (pipelineRun.flagDebug && !['PASSED', 'FAILED'].includes(pipelineRun.status))" avatar>
                      <q-icon v-if="waitingStepPos === i" name="play_circle_outline" color="primary" size="34px"
                        class="cursor-pointer" @click="onClickForwardRun" />
                      <q-icon v-else :name="stepRun.type == 'remote-prepare-env' ? 'computer' : 'account_tree'" color="black" size="34px" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label>
                        <span class="vertical-middle q-mr-sm">步骤 {{i+1}}</span>
                        <span class="vertical-middle">{{ stepRun.name }}</span>
                      </q-item-label>
                      <q-item-label caption>{{ stepRun.type }}</q-item-label>
                    </q-item-section>
                    <q-item-section class="text-center">
                      <span :class="{
                        'text-grey': stepRun.status == 'CREATED',
                        'text-orange': stepRun.status == 'STARTED',
                        'text-green': stepRun.status == 'PASSED',
                        'text-red': stepRun.status == 'FAILED',
                      }">
                        <template v-if="stepRun.status == 'STARTED'">
                          <q-spinner-facebook class="vertical-middle q-mr-xs" color="orange" />
                        </template>
                        <span class="vertical-middle">{{ stepRun.status }}</span>
                        <template v-if="stepRun.status == 'FAILED'">
                          <span>
                            <q-icon name="error" class="vertical-top" />
                            <q-tooltip class="bg-red">
                              {{ stepRun.error }}
                            </q-tooltip>
                          </span>
                        </template>
                      </span>
                    </q-item-section>
                    <q-item-section class="text-center gt-xs">
                      <span v-if="stepRun.beginTime">
                      {{ dayjs(stepRun.beginTime).format('YYYY-MM-DD HH:mm:ss') }} 启动
                      </span>
                    </q-item-section>
                    <q-item-section class="text-center gt-xs">
                      <span v-if="stepRun.beginTime">
                        持续 {{ dayjs.duration(stepRun.endTimeOrNow - stepRun.beginTime).format('HH:mm:ss') }}
                      </span>
                    </q-item-section>
                    <q-item-section side>
                      <div class="text-grey-8 q-gutter-xs">
                        <q-toggle v-model="stepRun.openLog" label="日志" size="xs" @update:model-value="(value) => onToggleOpenLog(value, i)" />
                        <q-btn class="gt-xs" flat dense round icon="download" size="sm" @click="onClickDownloadLog(i)" />
                      </div>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="stepRun.openLog">
                    <div class="full-width">
                      <q-linear-progress v-if="stepRun.status == 'STARTED'" indeterminate color="orange" />
                      <Logger :log="stepRun.log" :loading="stepRun.logLoading" :tail="flagTail" class="full-width" />
                    </div>
                  </q-item>
                  <q-item>
                      <q-toggle label="终端" size="xs" class="q-mr-sm" :model-value="!!terminalRuns[i]" @update:model-value="(value) => onToggleOpenTerminal(value, i)" />
                      <q-toggle label="浏览器" size="xs" :model-value="!!explorerRuns[i]" @update:model-value="(value) => onToggleOpenExplorer(value, i)" />
                  </q-item>
                  <q-item v-if="!!terminalRuns[i] || !!explorerRuns[i]">
                    <div class="row full-width">
                      <div :class="{
                        'col': true,
                        'col-12': $q.screen.lt.md,
                      }" v-if="!!terminalRuns[i]">
                        <Terminal :socketUrl="getTerminalRunAttachWsUrl(i)" />
                      </div>
                      <div :class="{
                        'col': true,
                        'col-12': $q.screen.lt.md,
                      }" v-if="!!explorerRuns[i]">
                        <Explorer :explorerRunInstanceUuid="explorerRuns[i]" :socketUrl="getExplorerRunAttachWsUrl(i)" />
                      </div>
                    </div>
                  </q-item>
                  <q-separator v-if="i+1 < pipelineStepRuns.length" />
                </q-list>
              </q-card> 
            </q-card>
          </template>
          <!-- <q-inner-loading :showing="loading">
            <q-spinner-gears size="30px" color="primary" />
          </q-inner-loading> -->
        </div>
        <q-page-scroller position="bottom-right" :scroll-offset="150" :offset="[15, 25]">
          <q-btn fab-mini icon="keyboard_arrow_up" color="primary" />
        </q-page-scroller>
      </div>
    </template>
    <template v-slot:right>
      <div class="text-grey q-mb-md">快捷操作</div>
      <q-list>
        <q-item clickable tag="a" :to="`/project/${projectId}/pipeline/${pipelineId}/run`">
          <q-item-section avatar>
            <q-icon name="shortcut" />
          </q-item-section>
          <q-item-section>执行历史</q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>

        <q-item clickable tag="a" :to="`/project/${projectId}/pipeline/${pipelineId}/step`">
          <q-item-section avatar>
            <q-icon name="shortcut" />
          </q-item-section>
          <q-item-section>编辑步骤</q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>

        <q-item clickable tag="a" :to="`/project/${projectId}/pipeline/${pipelineId}/run/create`">
          <q-item-section avatar>
            <q-icon name="shortcut" />
          </q-item-section>
          <q-item-section>重新启动</q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>
      </q-list>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.pipeline-run-detial-step{
  &-top{

    .title{
      width: 80px;
      color: #757575;
    }

    > .row{
      margin: 10px 0;
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
import Logger from '@/components/pipeline/logger/Logger'
import Terminal from '@/components/pipeline/terminal/Terminal'
import Explorer from '@/components/pipeline/explorer/Explorer'
import { ref, inject, computed, onMounted, onUnmounted } from 'vue'
import pipelineApi from '@/api/pipeline.api'
import logApi from '@/api/log.api'
import pipelineWs from '@/api/pipeline.ws'
import terminalWs from '@/api/terminal.ws'
import explorerWs from '@/api/explorer.ws'

export default {
  components: {
    LayoutTwoColumn,
    Logger,
    Terminal,
    Explorer,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, pipelineRunId } = route.params
    const { start, tail } = route.query
    const pipelineRun = ref({})
    const pipelineStepRuns = ref([])
    const terminalRuns = ref([])
    const explorerRuns = ref([])
    const pipelineRunWs = ref(null)
    const stopLoading = ref(false)
    const flagDetail = ref(false)
    const flagTail = ref(tail == 'true')
    const loading = ref(false)

    const fromUrl = computed(() => {
      const name = pipelineRun.value.name
      if(!name){
        return null
      }

      const i = name.indexOf('from:')
      if(i < 0){
        return null  
      }

      return `${name.slice(i+5)}`
    })

    const projectName = computed(() => {
      return store.state.project.name || ''
    })

    const runningStepPos = computed(() => {
      if(pipelineRun.value.status == 'RUNNING'){
        for(let i = 0; i < pipelineStepRuns.value.length; ++i){
          let stepRun = pipelineStepRuns.value[i]
          if(stepRun.status == 'STARTED'){
            return i
          }
        }
        return pipelineStepRuns.value.length - 1
      }else{
        return -1
      }
    })

    const waitingStepPos = computed(() => {
      if(pipelineRun.value.status == 'WAITING'){
        for(let i = 0; i < pipelineStepRuns.value.length; ++i){
          let stepRun = pipelineStepRuns.value[i]
          if(stepRun.status == 'CREATED'){
            return i
          }
        }
        return pipelineStepRuns.value.length
      }else{
        return -1
      }
    })

    const durationTimer = setInterval(()=>{

      // pipeline run duration
      const endTime = pipelineRun.value.endTime
      if(endTime){
        pipelineRun.value.endTimeOrNow = endTime
        clearInterval(durationTimer)
      }else{
        pipelineRun.value.endTimeOrNow = Date.now()
      }

      // step run duration
      pipelineStepRuns.value.forEach(stepRun => {
        stepRun.endTimeOrNow = stepRun.endTime || Date.now()
      })

    }, 1000)

    onMounted(() => {
      loading.value = true
      pipelineApi.getRun({ pipelineId, pipelineRunId}).then(resp => {
        pipelineRun.value = {
          ...resp.data.pipelineRun,
          endTimeOrNow: resp.data.pipelineRun.endTime || Date.now()
        },
        pipelineStepRuns.value = resp.data.pipelineStepRuns.map(r => {
          return {
            ...r,
            endTimeOrNow: r.endTime || Date.now(),
            openLog: false,
            log: '',
          }
        })

        // attach pipeline run
        if(pipelineRun.value.status != 'FAILED' && pipelineRun.value.status != 'PASSED'){
          pipelineWs.attachPipelineRun({
            projectId,
            pipelineId,
            pipelineRunId: pipelineRun.value.pipelineRunId,
          }, (status, error, outParams) => {
            // pipeline run status update
            pipelineRun.value.status = status
            pipelineRun.value.error = error
            pipelineRun.value.outParams = outParams
            switch(status){
              case 'STARTED':
                pipelineRun.value.beginTime = Date.now()
                break
              case 'PASSED':
                pipelineRun.value.endTime = Date.now()
                break
              case 'FAILED':
                pipelineRun.value.endTime = Date.now()
                break
            }
          }, (position, status, error) => {
            // step run status update
            pipelineStepRuns.value[position].status = status
            switch(status){
              case 'STARTED':
                pipelineStepRuns.value[position].beginTime = Date.now()
                break
              case 'PASSED':
                pipelineStepRuns.value[position].endTime = Date.now()
                break
              case 'FAILED':
                pipelineStepRuns.value[position].endTime = Date.now()
                pipelineStepRuns.value[position].error = error
                break
              case 'SKIPPED':
                pipelineStepRuns.value[position].beginTime = Date.now()
                pipelineStepRuns.value[position].endTime = Date.now()
                pipelineStepRuns.value[position].error = error
                break
            }
          }, (position, output) => {
            // step run output
            if(flagTail.value){
              pipelineStepRuns.value[position].openLog = true
            }
            if(pipelineStepRuns.value[position].openLog){
              pipelineStepRuns.value[position].log += output
            }
          }, (terminalRunInstanceUuid, status) => {
            let i = terminalRuns.value.indexOf(terminalRunInstanceUuid)
            if(i >= 0){
              if(status == 'STOPPED'){
                terminalRuns.value[i] = null
              }
            }
          }, (explorerRunInstanceUuid, status) => {
            let i = explorerRuns.value.indexOf(explorerRunInstanceUuid)
            if(i >= 0){
              if(status == 'STOPPED'){
                explorerRuns.value[i] = null
              }
            }
          }).then((ws) => {
            pipelineRunWs.value = ws

            if(start == 'true'){
              pipelineApi.startRun({
                pipelineId,
                pipelineRunId,
              })
            }
          })
        }

        store.commit('setTitle', { title: pipelineRun.value.name })
      }).finally(() => {
        loading.value = false
      })
    })

    onUnmounted(() => {
      if(pipelineRunWs.value){
        pipelineRunWs.value.close()
      }

      terminalRuns.value.forEach(terminalRunInstanceUuid => {
        if(terminalRunInstanceUuid){
          pipelineApi.destroyTerminalRun({
            pipelineId,
            pipelineRunId,
            terminalRunInstanceUuid,
          })
        }
      })

      explorerRuns.value.forEach(explorerRunInstanceUuid => {
        if(explorerRunInstanceUuid){
          pipelineApi.destroyExplorerRun({
            pipelineId,
            pipelineRunId,
            explorerRunInstanceUuid,
          })
        }
      })
    })

    return {
      $q,
      dayjs,

      projectId,
      projectName,
      pipelineId,
      pipelineName,
      pipelineRunId,
      pipelineRun,
      pipelineStepRuns,
      terminalRuns,
      explorerRuns,
      runningStepPos,
      waitingStepPos,
      pipelineRunWs,
      stopLoading,
      loading,
      flagDetail,
      flagTail,
      fromUrl,

      onClickBack(){
        router.back()
      },

      getTerminalRunAttachWsUrl(i){
        const terminalRunInstanceUuid = terminalRuns.value[i]
        const socketUrl = terminalWs.getTerminalRunAttachWsUrl({ projectId, pipelineId, pipelineRunId, terminalRunInstanceUuid })
        return socketUrl
      },

      getExplorerRunAttachWsUrl(i){
        const explorerRunInstanceUuid = explorerRuns.value[i]
        const socketUrl = explorerWs.getExplorerRunAttachWsUrl({ projectId, pipelineId, pipelineRunId, explorerRunInstanceUuid })
        return socketUrl
      },

      onToggleOpenTerminal(value, i){
        if(value){
          pipelineApi.createTerminalRun({
            pipelineId,
            pipelineRunId,
          }).then(resp => {
            const terminalRunInstanceUuid = resp.data
            terminalRuns.value[i] = terminalRunInstanceUuid
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }else{
          const terminalRunInstanceUuid = terminalRuns.value[i]
          if(terminalRunInstanceUuid){
            pipelineApi.destroyTerminalRun({
              pipelineId,
              pipelineRunId,
              terminalRunInstanceUuid,
            }).then(resp => {
              
            }, resp => {
              qUtil.notifyError(resp.message)
            })

            terminalRuns.value[i] = null
          }
        }
      },

      onToggleOpenExplorer(value, i){
        if(value){
          pipelineApi.createExplorerRun({
            pipelineId,
            pipelineRunId,
          }).then(resp => {
            const explorerRunInstanceUuid = resp.data
            explorerRuns.value[i] = explorerRunInstanceUuid
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }else{
          const explorerRunInstanceUuid = explorerRuns.value[i]
          if(explorerRunInstanceUuid){
            pipelineApi.destroyExplorerRun({
              pipelineId,
              pipelineRunId,
              explorerRunInstanceUuid,
            }).then(resp => {
              
            }, resp => {
              qUtil.notifyError(resp.message)
            })

            explorerRuns.value[i] = null
          }
        }
      },

      onClickForwardRun(){
        pipelineApi.forwardRun({
          pipelineId,
          pipelineRunId,
        }).then(resp => {}, resp => {
          qUtil.notifyError(resp.message)
        })
      },

      onToggleOpenLog(value, i){
        const stepRun = pipelineStepRuns.value[i]

        if(value){
          stepRun.logLoading = true
          logApi.getLog({ 
            pipelineId, 
            pipelineRunId: pipelineRun.value.pipelineRunId, 
            position: i
          }).then(resp => {
            const log = resp.data
            stepRun.log = log
          }).finally(() => {
            stepRun.logLoading = false
          })
        }else{
          stepRun.log = ''
        }
      },

      onClickDownloadLog(position){
        logApi.downloadLog({
          projectId,
          pipelineId,
          pipelineRunId, 
          position,
        })
      },

      onClickStopPipelineRun(){
        stopLoading.value = true
        pipelineApi.stopRun({ pipelineId, pipelineRunId }).then(resp => {
          
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(() => {
          stopLoading.value = false
        })
      },

    }
  }
};
</script>