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
  getTeam(){
    return request().get(`${API_AUTH_PREFIX}/team/get`)
  },
  listTeamMember({ username = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AUTH_PREFIX}/team/member/list?username=${encodeURIComponent(username)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  deleteTeamMember({ memberUserId }){
    return request().post(`${API_AUTH_PREFIX}/team/member/delete`, {
      memberUserId,
    })
  },
  listJoinedTeam({ pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AUTH_PREFIX}/team/joined/list?pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  leaveTeam({ leaveTeamId }){
    return request().post(`${API_AUTH_PREFIX}/team/leave`, {
      leaveTeamId,
    })
  },
  createTeamJoinToken(){
    return request().post(`${API_AUTH_PREFIX}/team/join/token/create`)
  },
  getTeamJoinToken({ joinTokenHashed }){
    return request().get(`${API_AUTH_PREFIX}/team/join/token/get?joinTokenHashed=${joinTokenHashed}`)
  },
  joinTeam({ joinTokenHashed }){
    return request().post(`${API_AUTH_PREFIX}/team/join`, {
      joinTokenHashed,
    })
  },
}
