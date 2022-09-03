import { request } from 'common'
const API_QUOTA_PREFIX = '/quota/api/user'

export default {
  getQuotaUsage(){
    return request().get(`${API_QUOTA_PREFIX}/quota/usage`)
  },
  getCurrentQuota(){
    return request().get(`${API_QUOTA_PREFIX}/quota/current`)
  },
}
