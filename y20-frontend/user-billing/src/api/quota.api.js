import { request } from 'common'
const API_QUOTA_PREFIX = '/quota/api/user'

export default {
  getFreeQuota(){
    return request().get(`${API_QUOTA_PREFIX}/quota/free`)
  },
  getQuotaUsage(){
    return request().get(`${API_QUOTA_PREFIX}/quota/usage`)
  },
  getCurrentQuota(){
    return request().get(`${API_QUOTA_PREFIX}/quota/current`)
  },
  list({ orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_QUOTA_PREFIX}/quota/list?pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
}
