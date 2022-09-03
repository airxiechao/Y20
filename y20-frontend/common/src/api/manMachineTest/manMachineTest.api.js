import request from '@/utils/globalRequest'

const API_MAN_MACHINE_TEST_PREFIX = '/manmachinetest/api/user'

export default {

  nextQuestion(){
    return request().get(`${API_MAN_MACHINE_TEST_PREFIX}/question/next`)
  },

  checkAnswer({ token, answer }){
    return request().post(`${API_MAN_MACHINE_TEST_PREFIX}/question/check`, {
      token,
      answer,
    })
  }

}