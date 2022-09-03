import { request } from 'common'

const API_AUTH_PREFIX = '/auth/api/user'

export default {

  loginByUsername({ username, password }){
    return request().post(`${API_AUTH_PREFIX}/login/username`, {
      username,
      password,
    })
  },

  loginByMobile({ mobile, verificationCodeToken, verificationCode }){
    return request().post(`${API_AUTH_PREFIX}/login/mobile`, {
      mobile,
      verificationCodeToken,
      verificationCode,
    })
  },

  loginByTwoFactor({ twoFactorToken, twoFactorCode }){
    return request().post(`${API_AUTH_PREFIX}/login/twofactor`, {
      twoFactorToken,
      twoFactorCode,
    })
  },

}