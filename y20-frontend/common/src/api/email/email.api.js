import request from '@/utils/globalRequest'

const API_EMAIL_PREFIX = '/email/api/user'

export default {

  sendVerificationCode({ token, answer, email }){
    return request().post(`${API_EMAIL_PREFIX}/email/verificationcode/send`, {
      manMachineTestQuestionToken: token,
      manMachineTestAnswer: answer,
      email,
    })
  }

}