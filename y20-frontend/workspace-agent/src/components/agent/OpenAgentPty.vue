<template>
  <LayoutTwoColumn class="open-agent-pty">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="节点" to="/workspace/agent" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="bg-white q-pa-md">
            <div class="q-pb-md page-heading">
              <div>节点终端 - {{agentId}}</div>
              <q-breadcrumbs class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el label="最长保持24小时"/>
              </q-breadcrumbs>
            </div>
            <div class="row full-width">
              <div class="col relative-position" >
                <q-inner-loading :showing="startLoading" dark>
                  <q-spinner-gears class="z-top" size="38px" color="grey-2" />
                </q-inner-loading>
                <Terminal v-if="!terminalRuns[0]" />
                <Terminal v-else :socketUrl="getTerminalRunAttachWsUrl(0)" />
              </div>
            </div>
            <div class="q-mt-md">
              <q-btn
                v-if="['PASSED', 'FAILED'].includes(pipelineRun.status)"
                unelevated
                color="green" 
                icon="play_arrow"
                label="打开"
                :loading="startLoading"
                @click="onClickStartPipelineRun"
              />
              <q-btn
                v-else
                unelevated
                color="red" 
                icon="stop"
                label="关闭"
                :loading="stopLoading"
                :disable="!terminalRuns[0]"
                @click="onClickStopPipelineRun"
              />
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.open-agent-pty{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { ref, inject, onMounted, onUnmounted } from 'vue'

import { LayoutTwoColumn } from 'common'
import Terminal from '@/components/agent/terminal/Terminal'

import pipelineApi from '@/api/pipeline.api'
import pipelineWs from '@/api/pipeline.ws'
import terminalWs from '@/api/terminal.ws'

export default {
  components: {
    LayoutTwoColumn,
    Terminal,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId, agentId } = route.params

    const pipelineRun = ref({})
    const pipelineStepRuns = ref([])
    const terminalRuns = ref([])
    const pipelineRunWs = ref(null)

    const startLoading = ref(true)
    const stopLoading = ref(false)

    const start = () => {
      startLoading.value = true

      // 创建
      pipelineApi.createRunPty({
        agentId
      }).then(resp => {
        const pipelineRunId = resp.data
        
        pipelineApi.getRun({ projectId: 0, pipelineId: 0, pipelineRunId}).then(resp => {
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
            // 监听
            pipelineWs.attachPipelineRun({
              projectId: 0,
              pipelineId: 0,
              pipelineRunId: pipelineRun.value.pipelineRunId,
            }, (status, error, outParams) => {
              // pipeline run status update
              pipelineRun.value.status = status
              pipelineRun.value.error = error
              pipelineRun.value.outParams = outParams
              switch(status){
                case 'STARTED':
                  pipelineRun.value.beginTime = Date.now()
                  // 启动第一步
                  pipelineApi.forwardRun({
                    projectId: 0,
                    pipelineId: 0,
                    pipelineRunId,
                  }, resp => {
                    qUtil.notifyError(resp.message)
                  })
                  break
                case 'PASSED':
                  pipelineRun.value.endTime = Date.now()
                  break
                case 'FAILED':
                  pipelineRun.value.endTime = Date.now()
                  startLoading.value = false
                  qUtil.notifyError(error)
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
                  // 开启终端
                  if(position == 0){
                    pipelineApi.createTerminalRun({
                      projectId: 0,
                      pipelineId: 0,
                      pipelineRunId,
                    }).then(resp => {
                      startLoading.value = false

                      const terminalRunInstanceUuid = resp.data
                      terminalRuns.value[position] = terminalRunInstanceUuid
                    }, resp => {
                      qUtil.notifyError(resp.message)
                    })
                  }
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

              // 启动
              pipelineApi.startRun({
                projectId: 0,
                pipelineId: 0,
                pipelineRunId,
              }, resp => {
                qUtil.notifyError(resp.message)
              })
            })
          }
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      }, resp => {
        const code = resp.code
        startLoading.value = false
        switch(code){
          case 'ERROR_NO_ENOUGH_QUOTA':
            $q.dialog({
              title: '配额不足',
              message: `流水线无法启动，请购买配额`,
              cancel: '取消',
              ok: '前往',
              persistent: true
            }).onOk(() => {
              router.push(`/user/billing`)
            })
            break;
          default:
            qUtil.notifyError(resp.message)
            break;
        }
      })
    }

    const stop = () => {
      stopLoading.value = true
      if(pipelineRun.value.pipelineRunId ){
        pipelineApi.stopRun({
          projectId: 0,
          pipelineId: 0, 
          pipelineRunId: pipelineRun.value.pipelineRunId 
        }).then(resp => {
          qUtil.notifySuccess("终端已关闭")
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(() => {
          stopLoading.value = false
        })
      }
    }

    onMounted(() => {
      start()
    })

    onUnmounted(() => {
      if(pipelineRunWs.value){
        pipelineRunWs.value.close()
      }

      if(!['PASSED', 'FAILED'].includes(pipelineRun.value.status)){
        stop()
      }
    })

    return {
      agentId,

      pipelineRun,
      pipelineStepRuns,
      terminalRuns,
      pipelineRunWs,

      startLoading,
      stopLoading,

      onClickBack(){
        router.back();
      },

      onClickStartPipelineRun(){
        start()
      },

      onClickStopPipelineRun(){
        stop()
      },

      getTerminalRunAttachWsUrl(i){
        const terminalRunInstanceUuid = terminalRuns.value[i]
        const socketUrl = terminalWs.getTerminalRunAttachWsUrl({ 
          projectId: 0, 
          pipelineId: 0, 
          pipelineRunId: pipelineRun.value.pipelineRunId, 
          terminalRunInstanceUuid 
        })
        return socketUrl
      },

    }
  }
};
</script>