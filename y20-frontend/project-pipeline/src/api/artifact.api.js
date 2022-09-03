import { request } from 'common'
import { store } from 'common'

const API_ARTIFACT_PREFIX = '/artifact/api/user'

export default {

  // user file

  listUserFile({ path }){
    return request().get(`${API_ARTIFACT_PREFIX}/artifact/user/file/list?path=${encodeURIComponent(path)}`)
  },
  downloadUserFile({ path }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_ARTIFACT_PREFIX}/artifact/user/file/download?path=${encodeURIComponent(path)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
  removeUserFile({ path }){
    return request().post(`${API_ARTIFACT_PREFIX}/artifact/user/file/remove`, {
      path,
    })
  },

  // pipeline file

  listPipelineFile({ pipelineId }){
    return request().get(`${API_ARTIFACT_PREFIX}/artifact/pipeline/file/list?pipelineId=${pipelineId}`)
  },
  downloadPipelineFile({ projectId, pipelineId, name }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_ARTIFACT_PREFIX}/artifact/pipeline/file/download?projectId=${projectId}&pipelineId=${pipelineId}&name=${encodeURIComponent(name)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
  getUploadPipelineFileUrl(){
    return `${API_ARTIFACT_PREFIX}/artifact/pipeline/file/upload`
  },
  getUploadPipelineFileHeaders({ projectId }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    return [
      { name: 'auth', value: accessToken },
      { name: 'teamId', value: teamId },
      { name: 'projectId', value: projectId },
    ]
  },
  getUploadPipelineFileFields({ pipelineId, dir }){
    return [
      { name: 'pipelineId', value: pipelineId },
      { name: 'dir', value: dir },
    ]
  },
  removePipelineFile({ pipelineId, name }){
    return request().post(`${API_ARTIFACT_PREFIX}/artifact/pipeline/file/remove`, {
      pipelineId,
      name,
    })
  },

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

  // pipeline step file

  getUploadPipelineStepFileUrl(){
    return `${API_ARTIFACT_PREFIX}/artifact/pipeline/step/file/upload`
  },
  getUploadPipelineStepFileHeaders({ projectId }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    return [
      { name: 'auth', value: accessToken },
      { name: 'teamId', value: teamId },
      { name: 'projectId', value: projectId },
    ]
  },
  getUploadPipelineStepFileFields({ pipelineId }){
    return [
      { name: 'pipelineId', value: pipelineId },
    ]
  },

  // pipeline run file

  listPipelineRunFile({ pipelineId, pipelineRunId }){
    return request().get(`${API_ARTIFACT_PREFIX}/artifact/pipeline/run/file/list?pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}`)
  },
  downloadPipelineRunFile({ projectId, pipelineId, pipelineRunId, name }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_ARTIFACT_PREFIX}/artifact/pipeline/run/file/download?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&name=${encodeURIComponent(name)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
}