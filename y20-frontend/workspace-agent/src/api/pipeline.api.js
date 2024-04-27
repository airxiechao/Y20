import axios from 'axios';
import { request } from 'common'
import { store } from 'common'

const API_PIPELINE_PREFIX = '/pipeline/api/user'
const CancelToken = axios.CancelToken;

export default {

  // run

  getRun({ projectId, pipelineId, pipelineRunId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/run/get?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}`)
  },
  createRunPty({ agentId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/create/pty`, {
      agentId
    })
  },
  startRun({ projectId, pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/start`, {
      projectId, 
      pipelineId,
      pipelineRunId
    })
  },
  forwardRun({ projectId, pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/forward`, {
      projectId, 
      pipelineId,
      pipelineRunId
    })
  },
  stopRun({ projectId, pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/stop`, {
      projectId, 
      pipelineId,
      pipelineRunId
    })
  },
  deleteRun({ projectId, pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/delete`, {
      projectId, 
      pipelineId,
      pipelineRunId
    })
  },

  // terminal run

  createTerminalRun({ projectId, pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/create`, {
      projectId, 
      pipelineId,
      pipelineRunId,
    })
  },

  destroyTerminalRun({ projectId, pipelineId, pipelineRunId, terminalRunInstanceUuid }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/destroy`, {
      projectId, 
      pipelineId,
      pipelineRunId,
      terminalRunInstanceUuid,
    })
  },

  inputTerminalRun({ projectId, pipelineId, pipelineRunId, terminalRunInstanceUuid, message }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/message`, {
      projectId, 
      pipelineId,
      pipelineRunId,
      terminalRunInstanceUuid,
      message,
    })
  },

}
