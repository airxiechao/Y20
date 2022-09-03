import { request } from 'common'
const API_PAY_PREFIX = '/pay/api/user'

export default {
  getQuotaPrice(){
    return request().get(`${API_PAY_PREFIX}/pay/quota/price`)
  },
  orderQuotaQr({ billingPlan, numMonth, payType }){
    return request().post(`${API_PAY_PREFIX}/pay/quota/order`, {
      billingPlan,
      numMonth,
      payType,
    })
  },
  getQuotaOrder({ outTradeNo }){
    return request().get(`${API_PAY_PREFIX}/pay/quota/order/get?outTradeNo=${outTradeNo}`)
  },
}
