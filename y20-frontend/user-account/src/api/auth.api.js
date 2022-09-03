import { request } from 'common'
const API_AUTH_PREFIX = '/auth/api/user'

export default {
  getAccount(){
    return request().get(`${API_AUTH_PREFIX}/account/get`)
  },
  updateAccountPassword({ password, verificationCode, verificationCodeToken }){
    return request().post(`${API_AUTH_PREFIX}/account/password/update`, {
      password,
      verificationCode,
      verificationCodeToken,
    })
  },
  updateAccountMobile({ mobile, verificationCode, verificationCodeToken }){
    return request().post(`${API_AUTH_PREFIX}/account/mobile/update`, {
      mobile,
      verificationCode,
      verificationCodeToken,
    })
  },
  updateAccountEmail({ email, verificationCode, verificationCodeToken }){
    return request().post(`${API_AUTH_PREFIX}/account/email/update`, {
      email,
      verificationCode,
      verificationCodeToken,
    })
  },
  createAccountTwoFactorSecret(){
    return request().post(`${API_AUTH_PREFIX}/account/twofactor/secret/create`)
  },
  enableAccountTwoFactor({ code }){
    return request().post(`${API_AUTH_PREFIX}/account/twofactor/enable`, {
      code,
    })
  },
  disableAccountTwoFactor({ code }){
    return request().post(`${API_AUTH_PREFIX}/account/twofactor/disable`, {
      code,
    })
  },
}
