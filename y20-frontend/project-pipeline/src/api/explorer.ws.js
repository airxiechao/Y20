import { store } from 'common'

const PIPELINE_WS_PREFIX = 'pipeline/ws/user'

export default {
  getExplorerRunAttachWsUrl({ projectId, pipelineId, pipelineRunId, explorerRunInstanceUuid }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    let protocol = window.location.protocol.startsWith('https') ? 'wss' : 'ws'
    return `${protocol}://${location.host}/${PIPELINE_WS_PREFIX}/explorer/run/attach?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&explorerRunInstanceUuid=${explorerRunInstanceUuid}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`
  },
}