import { store } from 'common'

const PIPELINE_WS_PREFIX = 'pipeline/ws/user'

export default {
  /**
   * 绑定流水线运行事件监听
   */
  attachPipelineRun({ projectId, pipelineId, pipelineRunId }, 
      pipelineRunStatusUpdateListener,
      stepRunStatusUpdateListener,
      stepRunOutputListener,
      terminalRunStatusUpdateListener,
      explorerRunStatusUpdateListener,
  ) {
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    return new Promise((resolve, reject) => {
      let protocol = window.location.protocol.startsWith('https') ? 'wss' : 'ws'
      let pipelineRunWs = new WebSocket(`${protocol}://${location.host}${location.port?":"+location.port:""}/${PIPELINE_WS_PREFIX}/pipeline/run/attach?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`)

      // Connection opened
      pipelineRunWs.addEventListener('open', function (event) {
        console.log('pipeline run ws open')
        resolve(pipelineRunWs)
      })
  
      // Listen for messages
      pipelineRunWs.addEventListener('message', function (event) {
        let resp = JSON.parse(event.data)
        //console.log(resp)
        let type = resp.data.type
        switch(type){
          case 'y20_pipeline_run_status_update':
            let pipeineRunstatus = resp.data.status
            let pipeineRunError = resp.data.error
            let pipelineRunOutParams = resp.data.outParams
            if(pipelineRunStatusUpdateListener){
              pipelineRunStatusUpdateListener(pipeineRunstatus, pipeineRunError, pipelineRunOutParams)
            }

            if(['PASSED', 'FAILED'].includes(pipeineRunstatus)){
              pipelineRunWs.close()
            }
            break
          case 'y20_pipeline_step_run_status_update':
            let stepRunPosition = resp.data.position
            let stepRunStatus = resp.data.status
            let stepRunError = resp.data.error
            if(stepRunStatusUpdateListener){
              stepRunStatusUpdateListener(stepRunPosition, stepRunStatus, stepRunError)
            }
            break
          case 'y20_pipeline_step_run_output':
            let stepRunOutputPosition = resp.data.position
            let stepRunOutput = resp.data.output
            if(stepRunOutputListener){
              stepRunOutputListener(stepRunOutputPosition, stepRunOutput)
            }
            break
          case 'y20_pipeline_run_terminal_status_update':
            let terminalRunInstanceUuid = resp.data.terminalRunInstanceUuid
            let terminalRunInstanceStatus = resp.data.status
            if(terminalRunStatusUpdateListener){
              terminalRunStatusUpdateListener(terminalRunInstanceUuid, terminalRunInstanceStatus)
            }
            break
          case 'y20_pipeline_run_explorer_status_update':
            let explorerRunInstanceUuid = resp.data.explorerRunInstanceUuid
            let explorerRunInstanceStatus = resp.data.status
            if(explorerRunStatusUpdateListener){
              explorerRunStatusUpdateListener(explorerRunInstanceUuid, explorerRunInstanceStatus)
            }
            break
        }
        
      })
  
      // Listen for messages
      pipelineRunWs.addEventListener('close', function (event) {
        console.log('pipeline run ws close')
        reject()
      })
    })
    
  },
}