import { request } from 'common'
const API_AUTH_PREFIX = '/auth/api/user'

export default {
  listUserAccessToken({ pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AUTH_PREFIX}/access/token/user/list?pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  deleteAccessToken({ accessTokenId }){
    return request().post(`${API_AUTH_PREFIX}/access/token/delete`, {
      accessTokenId,
    })
  },
}
