import request from '@/common/request'
const API_PAY_PREFIX = '/pay/api/user'

export default {
  getQuotaPrice(){
    return request.get(`${API_PAY_PREFIX}/pay/quota/price`)
  },
}
