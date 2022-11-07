import { request } from 'common'
import { store } from 'common'

const API_ARTIFACT_PREFIX = '/artifact/api/user'

export default {

  // pipeline temp file

  getUploadPipelineTempFileUrl(){
    return `${API_ARTIFACT_PREFIX}/artifact/pipeline/temp/file/upload`
  },
  getUploadPipelineTempFileHeaders({ projectId }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    return [
      { name: 'auth', value: accessToken },
      { name: 'teamId', value: teamId },
      { name: 'projectId', value: projectId },
    ]
  },
  getUploadPipelineTempFileFields({ pipelineId }){
    return [
      { name: 'pipelineId', value: pipelineId },
    ]
  },

}