import { request } from 'common'
const API_PROJECT_PREFIX = '/project/api/user'

export default {
  list({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_PROJECT_PREFIX}/project/list?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  listVariable(){
    return request().get(`${API_PROJECT_PREFIX}/project/variable/list`)
  },
}