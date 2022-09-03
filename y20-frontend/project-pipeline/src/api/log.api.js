import { request } from 'common'
import { store } from 'common'
const API_LOG_PREFIX = '/log/api/user'

export default {

  getLog({ pipelineId, pipelineRunId, position }){
    return request().get(`${API_LOG_PREFIX}/log/get?pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&position=${position}`)
  },
  downloadLog({ projectId, pipelineId, pipelineRunId, position }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_LOG_PREFIX}/log/download?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&position=${position}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
}
