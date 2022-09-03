import { store } from 'common'

const PIPELINE_WS_PREFIX = 'pipeline/ws/user'

export default {
  getTerminalRunAttachWsUrl({ projectId, pipelineId, pipelineRunId, terminalRunInstanceUuid }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    let protocol = window.location.protocol.startsWith('https') ? 'wss' : 'ws'
    return `${protocol}://${location.host}/${PIPELINE_WS_PREFIX}/terminal/run/attach?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&terminalRunInstanceUuid=${terminalRunInstanceUuid}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`
  },
}