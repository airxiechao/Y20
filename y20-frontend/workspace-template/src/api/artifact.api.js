import { request } from 'common'
import { store } from 'common'

const API_ARTIFACT_PREFIX = '/artifact/api/user'

export default {

  // template file

  listTemplateFile({ templateId, path }){
    path = path ? encodeURIComponent(path) : ''
    return request().get(`${API_ARTIFACT_PREFIX}/artifact/template/file/list?templateId=${templateId}&path=${path}`)
  },
  downloadTemplateFile({ templateId, path }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_ARTIFACT_PREFIX}/artifact/template/file/download?templateId=${templateId}&path=${encodeURIComponent(path)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },
}