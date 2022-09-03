import { request } from 'common'
const API_AUTH_PREFIX = '/auth/api/user'

export default {
  logout(){
    return request().post(`${API_AUTH_PREFIX}/logout`)
  },
  listJoinedTeam(){
    return request().get(`${API_AUTH_PREFIX}/team/joined/list`)
  }
}