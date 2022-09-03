import request from '@/common/request'
const API_QUOTA_PREFIX = '/quota/api/user'

export default {
  getFreeQuota(){
    return request.get(`${API_QUOTA_PREFIX}/quota/free`)
  },
}
