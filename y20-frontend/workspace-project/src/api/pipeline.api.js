import { request } from 'common'

const API_PIPELINE_PREFIX = '/pipeline/api/user'

export default {
  listRunRunning(){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/run/running/list`)
  },
  createHello({ projectId, name, flagOneRun }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/create/hello`, {
      projectId,
      name,
      flagOneRun,
    })
  },
  createRun({ projectId, pipelineId, name, inParams, flagDebug }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/create`, {
      projectId,
      pipelineId, 
      name,
      inParams,
      flagDebug
    })
  },
}
