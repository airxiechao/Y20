<template>
  <q-form
    @submit="onSubmit"
    class="q-gutter-md"
  >
    <q-input
      outlined
      v-model="mobile"
      :label="`${$t('label-mobile')}`"
      :hint="`${$t('error-no-mobile')}`"
      lazy-rules
      :rules="[
        val => val !== null && val !== '' || $t('error-no-mobile'),
        val => validateMobile(val) || $t('error-mobile-not-valid'),
      ]"
    />

    <q-input
      class="q-pr-xs"
      outlined
      v-model="verificationCode"
      :label="`${$t('label-verification-code')}`"
      :hint="`${$t('error-no-verification-code')}`"
      lazy-rules
      :rules="[
        val => val !== null && val !== '' || $t('error-no-verification-code')
      ]"
    >
      <template v-slot:after>
        <BtnSendSmsVerificationCode />
      </template>
    </q-input>

    <div class="q-mt-md">
      <q-btn unelevated class="full-width" size="lg" :label="$t('label-login')" :loading="flagLoginLoading" type="submit" color="primary"/>
    </div>

  </q-form>
</template>

<i18n>
{
  "zhCN": {
    "label-mobile": "手机号",
    "error-no-mobile": "请输入手机号",
    "error-mobile-not-valid": "请输入有效的手机号",
    "label-verification-code": "验证码",
    "error-no-verification-code": "请输入验证码",
    "label-login": "登录",
  }
}
</i18n>

<script>
import { PAGE_NAME } from '@/page.config.js'
import { ref, inject, provide, computed } from 'vue'
import authApi from '@/api/auth.api'

import { BtnSendSmsVerificationCode } from 'common'

export default {
  components: {
    BtnSendSmsVerificationCode
  },
  setup(){
    const useStore = inject('useStore')
    const store = useStore()

    const useRouter = inject('useRouter')
    const router = useRouter()

    const useQuasar = inject('useQuasar')
    const $q = useQuasar()
    const qUtil = inject('quasarUtil')($q)

    const mobile = ref(null)
    const verificationCodeToken = ref(null)
    const verificationCode = ref(null)
    const flagLoginLoading = ref(false)

    const pathBeforeLogin = computed(() => {
      return store.state['pathBeforeLogin']
    })

    provide('mobile', mobile)
    provide('verificationCodeToken', verificationCodeToken)

    return {
      mobile,
      verificationCodeToken,
      verificationCode,
      flagLoginLoading,

      validateMobile(val){
        return (/^([0-9]{11})$/.test(val))
      },

      onSubmit(){
        flagLoginLoading.value = true
        authApi.loginByMobile({
          mobile: mobile.value,
          verificationCodeToken: verificationCodeToken.value,
          verificationCode: verificationCode.value,
        }).then(resp => {
          // login passed
          const { username, accessToken } = resp.data
          store.commit('setLogin', {username, accessToken})

          // redirect to home
          if(pathBeforeLogin.value && pathBeforeLogin.value.path != '/'){
            router.push(pathBeforeLogin.value)
          }else{
            router.push({ path: '/workspace/project' })
          }
        }, resp => {
          // login failed
          const code = resp.code
          const twoFactorToken = resp.data
          switch(code){
            case 'ERROR_NEED_TWO_FACTOR':
              store.commit('login/setTwoFactorToken', { twoFactorToken })
              router.push('/login/twofactor')
              break;
            default:
              qUtil.notifyError('手机号或验证码错误')
              break;
          }
        }).finally(() => {
          flagLoginLoading.value = false
        })
      },
    }
  }
}
</script>
