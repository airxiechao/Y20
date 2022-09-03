import { request } from 'common'

const API_AUTH_PREFIX = '/auth/api/user'

export default {

  signup({ username, password, mobile, verificationCodeToken, verificationCode }){
    return request().post(`${API_AUTH_PREFIX}/signup`, {
      username,
      password,
      mobile,
      verificationCodeToken,
      verificationCode,
    })
  }

}