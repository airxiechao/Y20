import { request } from 'common'
const API_MONITOR_PREFIX = '/monitor/api/user'

export default {
  list(){
    return request().get(`${API_MONITOR_PREFIX}/monitor/list`)
  },
}
