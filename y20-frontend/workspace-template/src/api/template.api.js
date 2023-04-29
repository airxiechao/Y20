import { request } from 'common'
const API_TEMPLATE_PREFIX = '/template/api/user'

export default {
  getBasic({ templateId }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/basic/get?templateId=${templateId}`)
  },
  getFull({ templateId }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/full/get?templateId=${templateId}`)
  },
  list({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/list?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  listMy({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/list/my?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  recommend({ pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/recommend?pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  delete({ templateId }){
    return request().post(`${API_TEMPLATE_PREFIX}/template/delete`, {
      templateId,
    })
  },
  apply({ templateId, toProjectId, name, flagOneRun }){
    return request().post(`${API_TEMPLATE_PREFIX}/template/apply`, {
      templateId,
      toProjectId,
      name,
      flagOneRun,
    })
  },
}
