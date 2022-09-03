<template>
  <q-btn icon="send" :disabled="flagSendVerificationCodeDisabled || (validate && !validateMobile(mobile))"
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
import smsApi from '@/api/sms/sms.api'
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

    const mobile = inject('mobile') || ref('')
    const verificationCodeToken = inject('verificationCodeToken')
    const flagSendVerificationCodeDisabled = ref(false)
    const flagSendVerificationCodeLoading = ref(false)
    const numVerificationCodeCountDown = ref(0)
    const refPopupManMachineTest = ref(null)

    return {
      mobile,
      verificationCodeToken,
      flagSendVerificationCodeDisabled,
      flagSendVerificationCodeLoading,
      numVerificationCodeCountDown,
      refPopupManMachineTest,

      validateMobile(val){
        return (/^([0-9]{11})$/.test(val))
      },

      onPassedManMachineTest({ token, answer }) {
        refPopupManMachineTest.value.hide()
        flagSendVerificationCodeLoading.value = true

        // send verification code
        smsApi.sendVerificationCode({ token, answer, mobile: mobile.value }).then(resp => {
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
          
          qUtil.notifySuccess('手机验证码已发送，请注意查收')
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