import { request } from 'common'
const API_PROJECT_PREFIX = '/monitor/api/user'

export default {
  list({ name = '', orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PROJECT_PREFIX}/monitor/list?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  get({ monitorId }){
    return request().get(`${API_PROJECT_PREFIX}/monitor/get?monitorId=${monitorId}`)
  },
  create({ name, agentId, type, target }){
    return request().post(`${API_PROJECT_PREFIX}/monitor/create`, {
      name,
      agentId,
      type,
      target,
    })
  },
  updateBasic({ monitorId, name, agentId, type, target }){
    return request().post(`${API_PROJECT_PREFIX}/monitor/basic/update`, {
      monitorId,
      name,
      agentId,
      type,
      target,
    })
  },
  delete({ monitorId }){
    return request().post(`${API_PROJECT_PREFIX}/monitor/delete`, {
      monitorId,
    })
  },
}