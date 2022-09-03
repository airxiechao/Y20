import { request } from 'common'
const API_AUTH_PREFIX = '/auth/api/user'

export default {
  createAgentAccessToken({ name, beginTime, endTime }){
    return request().post(`${API_AUTH_PREFIX}/access/token/agent/create`, {
      name,
      beginTime,
      endTime,
    })
  },
}
