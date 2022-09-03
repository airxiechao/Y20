import { request } from 'common'
const API_PROJECT_PREFIX = '/project/api/user'

export default {
  get({ projectId }){
    return request().get(`${API_PROJECT_PREFIX}/project/basic/get?projectId=${projectId}`)
  },
}
