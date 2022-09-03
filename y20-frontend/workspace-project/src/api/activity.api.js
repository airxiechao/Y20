import { request } from 'common'
const API_ACTIVITY_PREFIX = '/activity/api/user'

export default {
  list({ pageNo = '', pageSize = ''}){
    return request().get(`${API_ACTIVITY_PREFIX}/activity/list?pageNo=${pageNo}&pageSize=${pageSize}`)
  },
}
