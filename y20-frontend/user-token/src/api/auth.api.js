import { request } from 'common'
const API_AUTH_PREFIX = '/auth/api/user'

export default {
  listNotUserAccessToken({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AUTH_PREFIX}/access/token/notuser/list?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  createAgentAccessToken({ name, beginTime, endTime }){
    return request().post(`${API_AUTH_PREFIX}/access/token/agent/create`, {
      name,
      beginTime,
      endTime,
    })
  },
  createWebhookAccessToken({ name, beginTime, endTime }){
    return request().post(`${API_AUTH_PREFIX}/access/token/webhook/create`, {
      name,
      beginTime,
      endTime,
    })
  },
  deleteAccessToken({ accessTokenId }){
    return request().post(`${API_AUTH_PREFIX}/access/token/delete`, {
      accessTokenId,
    })
  },
}
