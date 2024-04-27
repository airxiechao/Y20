<template>
  <div>
    <div class="text-center text-h5 q-mb-lg">
      <a href="/">
        <q-icon class="q-mr-sm vertical-middle" size="4rem" :name="`img:/icon-logo.png`" />
        <span class="vertical-middle text-black text-weight-bold">鲲擎运维</span>
      </a>
    </div>
    
    <q-form
      @submit="onSubmit"
      @reset="onReset"
      class="q-mt-md q-gutter-md"
    >
    
      <div class="text-center text-h6"><router-link class="text-dark" to="/signup" style="text-decoration: none;">新用户注册</router-link></div>          
            
      <q-input
        outlined
        v-model="username"
        :label="`${$t('label-username')} *`"
        :hint="$t('error-username-format')"
        lazy-rules
        :rules="[ 
            val => val && val.length > 0 || $t('error-no-username'),
            val => /^[a-zA-Z0-9-_]{3,50}$/.test(val) || $t('error-username-format')
        ]"
      />

      <q-input
        v-if="ENABLE_SIGNUP_MOBILE"
        outlined
        v-model="mobile"
        :label="`${$t('label-mobile')} *`"
        :hint="`${$t('error-no-mobile')}`"
        lazy-rules
        :rules="[
          val => val !== null && val !== '' || $t('error-no-mobile'),
          val => validateMobile(val) || $t('error-mobile-not-valid'),
        ]"
      />

      <q-input 
        v-if="ENABLE_SIGNUP_MOBILE"
        outlined
        v-model="verificationCode"
        :label="`${$t('label-verification-code')} *`"
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

      <q-input
        outlined
        type="password"
        autocomplete="new-password"
        v-model="password"
        :label="`${$t('label-password')} *`"
        :hint="$t('error-password-length')"
        lazy-rules
        :rules="[
          val => val !== null && val !== '' || $t('error-no-password'),
          val => val.length >= 6 && val.length <= 50 || $t('error-password-length'),
        ]"
      />

      <q-input
        outlined
        type="password"
        v-model="password2"
        :label="`${$t('label-password2')} *`"
        :hint="$t('error-no-password2')"
        lazy-rules
        :rules="[
          val => val !== null && val !== '' || $t('error-no-password2'),
          val => val === password || $t('error-not-match-password'),
        ]"
      />

      <div>
        <q-toggle class="vertical-middle" v-model="accept" :label="$t('label-accept')" />
        <a class="text-primmary vertical-middle" target="_blank" href="/docs/terms-of-service.html">《服务条款》</a>
      </div>
      
      <div>
        <q-btn unelevated class="full-width" size="lg" :label="$t('label-signup')" :loading="flagSignupLoading" type="submit" color="primary"/>
      </div>

      <div class="q-mt-lg text-center text-grey">已有账号？<q-btn color="primary" dense flat label="登录" type="a" to="/login" /></div>

    </q-form>
  </div>
  
</template>

<i18n>
{
  "zhCN": {
    "label-username": "用户名",
    "error-no-username": "请输入用户名",
    "error-username-format": "用户名为3~50个字符（只含：a-zA-Z0-9-_）",
    "label-mobile": "手机号",
    "error-no-mobile": "请输入手机号",
    "error-mobile-not-valid": "请输入有效的手机号",
    "label-accept": "我同意遵守",
    "label-signup": "注册",
    "label-reset": "重置",
    "label-verification-code": "验证码",
    "error-no-verification-code": "请输入验证码",
    "label-password": "设置密码",
    "error-no-password": "请输入密码",
    "error-password-length": "密码长度为6~50个字符",
    "label-password2": "重复密码",
    "error-no-password2": "请再次输入密码",
    "error-not-match-password": "两次输入的密码不一致",
  },
}
</i18n>


<script>
import authApi from '@/api/auth/auth.api'
import { BtnSendSmsVerificationCode } from 'common'
import { ref, inject, provide, onMounted } from 'vue'

export default {
  components: {
    BtnSendSmsVerificationCode
  },
  setup (props, context) {
    const useQuasar = inject('useQuasar')
    const $q = useQuasar()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()

    const username = ref(null)
    const mobile = ref(null)
    const verificationCodeToken = ref(null)
    const verificationCode = ref(null)
    const password = ref(null)
    const password2 = ref(null)
    const accept = ref(false)

    const flagSignupLoading = ref(false)

    provide('mobile', mobile)
    provide('verificationCodeToken', verificationCodeToken)

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,
      ENABLE_SIGNUP_MOBILE: ENABLE_SIGNUP_MOBILE,

      username,
      mobile,
      verificationCodeToken,
      verificationCode,
      password,
      password2,
      accept,
      flagSignupLoading,

      validateMobile(val){
        return (/^([0-9]{11})$/.test(val))
      },

      onSubmit () {
        if (accept.value !== true) {
          qUtil.notifyError("请同意遵守服务条款")
          return
        }
        
        flagSignupLoading.value = true
        authApi.signup({ 
          username: username.value,
          password: password.value,
          mobile: mobile.value,
          verificationCodeToken: verificationCodeToken.value,
          verificationCode: verificationCode.value,
         }).then(resp => {
           //qUtil.notifySuccess(resp.message || '注册完成')
           router.push('/signup/success')
         }, resp => {
           qUtil.notifyError(resp.message)
         }).finally(()=>{
           flagSignupLoading.value = false
         })
      },

      onReset () {
        username.value = null
        mobile.value = null
        verificationCode.value = null
        verificationCodeToken.value = null
        password.value = null
        password2.value = null
        accept.value = false
      },
    }
  }
};
</script>

