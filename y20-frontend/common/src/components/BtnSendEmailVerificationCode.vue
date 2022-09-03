<template>
  <q-btn icon="email" :disabled="flagSendVerificationCodeDisabled || (validate && !validateEmail(email))"
      :loading="flagSendVerificationCodeLoading"
      :label="$t('label-send-verification-code') + ' ' + (numVerificationCodeCountDown > 0 ? numVerificationCodeCountDown : '')"
  >
    <q-popup-proxy ref="refPopupManMachineTest" anchor="center right" self="center left" :offset="[10, 0]">
      <ManMachineTest :points="[]" @passed="onPassedManMachineTest" />  
    </q-popup-proxy>
  </q-btn>
</template>


<i18n>
{
  "zhCN": {
    "label-send-verification-code": "发送验证码",
  },
}
</i18n>

<script>
import { ref, inject } from 'vue'
import emailApi from '@/api/email/email.api'
import ManMachineTest from '@/components/ManMachineTest'

export default {
  props: {
    validate: {
      default: true
    },
  },
  components: {
    ManMachineTest,
  },
  setup(){
    const useQuasar = inject('useQuasar')
    const $q = useQuasar()
    const qUtil = inject('quasarUtil')($q)

    const email = inject('email') || ref('')
    const verificationCodeToken = inject('verificationCodeToken')
    const flagSendVerificationCodeDisabled = ref(false)
    const flagSendVerificationCodeLoading = ref(false)
    const numVerificationCodeCountDown = ref(0)
    const refPopupManMachineTest = ref(null)

    return {
      email,
      verificationCodeToken,
      flagSendVerificationCodeDisabled,
      flagSendVerificationCodeLoading,
      numVerificationCodeCountDown,
      refPopupManMachineTest,

      validateEmail(val){
        return (/^[^@]+@[^@]+$/.test(val))
      },

      onPassedManMachineTest({ token, answer }) {
        refPopupManMachineTest.value.hide()
        flagSendVerificationCodeLoading.value = true

        // send verification code
        emailApi.sendVerificationCode({ token, answer, email: email.value }).then(resp => {
          // send ok
          verificationCodeToken.value = resp.data

          flagSendVerificationCodeDisabled.value = true
          numVerificationCodeCountDown.value = 60

          const countDown = () => {
            setTimeout(() => {
              numVerificationCodeCountDown.value -= 1
              if(numVerificationCodeCountDown.value > 0){
                countDown()
              }else{
                flagSendVerificationCodeDisabled.value = false
              }
            }, 1000)
          }
          countDown()
          
          qUtil.notifySuccess('邮箱验证码已发送，请注意查收')
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(() => {
          flagSendVerificationCodeLoading.value = false
        })
      },
    }
  }
}
</script>