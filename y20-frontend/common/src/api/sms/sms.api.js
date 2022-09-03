import request from '@/utils/globalRequest'

const API_SMS_PREFIX = '/sms/api/user'

export default {

  sendVerificationCode({ token, answer, mobile }){
    return request().post(`${API_SMS_PREFIX}/sms/verificationcode/send`, {
      manMachineTestQuestionToken: token,
      manMachineTestAnswer: answer,
      mobile,
    })
  }

}