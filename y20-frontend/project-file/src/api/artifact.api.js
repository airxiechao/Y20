import { request } from 'common'
import { store } from 'common'

const API_ARTIFACT_PREFIX = '/artifact/api/user'

export default {
  listProjectFile(){
    return request().get(`${API_ARTIFACT_PREFIX}/artifact/project/file/list`)
  },
  downloadProjectFile({ projectId, name }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_ARTIFACT_PREFIX}/artifact/project/file/download?projectId=${projectId}&name=${encodeURIComponent(name)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
  getUploadProjectFileUrl(){
    return `${API_ARTIFACT_PREFIX}/artifact/project/file/upload`
  },
  getUploadProjectFileHeaders({ projectId }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    return [
      { name: 'auth', value: accessToken },
      { name: 'teamId', value: teamId },
      { name: 'projectId', value: projectId },
    ]
  },
  getUploadProjectFileFields({ dir }){
    return [
      { name: 'dir', value: dir },
    ]
  },
  removeProjectFile({ name }){
    return request().post(`${API_ARTIFACT_PREFIX}/artifact/project/file/remove`, {
      name,
    })
  },

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
}