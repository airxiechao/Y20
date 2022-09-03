<template>
  <LayoutTwoColumn class="update-account-mobile">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="账号" to="/user/account" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">绑定新手机</div>
            <q-form
              @submit="onSubmit"
              @reset="onReset"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="mobile"
                :label="`${$t('label-mobile')} *`"
                lazy-rules
                :rules="[
                  val => val !== null && val !== '' || $t('error-no-mobile'),
                  val => validateMobile(val) || $t('error-mobile-not-valid'),
                ]"
              />

              <q-input
                outlined
                v-model="verificationCode"
                :label="`${$t('label-verification-code')} *`"
                lazy-rules
                :rules="[
                  val => val !== null && val !== '' || $t('error-no-verification-code')
                ]"
              >
                <template v-slot:after>
                  <BtnSendSmsVerificationCode />
                </template>
              </q-input>

              <div>
                <q-btn unelevated :label="$t('label-bind')" :loading="flagBindLoading" type="submit" color="primary"/>
              </div>
            </q-form>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<i18n>
{
  "zhCN": {
    "label-mobile": "手机号",
    "error-no-mobile": "请输入手机号",
    "error-mobile-not-valid": "请输入有效的手机号",
    "label-verification-code": "手机验证码",
    "error-no-verification-code": "请输入手机验证码",
    "label-send-verification-code": "发送验证码",
    "label-bind": "绑定",
    "label-reset": "重置",
  },
}
</i18n>

<script>
import { LayoutTwoColumn, BtnSendSmsVerificationCode } from 'common'
import { ref, inject, provide, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
    BtnSendSmsVerificationCode,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const mobile = ref(null)
    const verificationCodeToken = ref(null)
    const verificationCode = ref(null)
    const flagBindLoading = ref(false)

    provide('mobile', mobile)
    provide('verificationCodeToken', verificationCodeToken)

    return {
      mobile,
      verificationCodeToken,
      verificationCode,
      flagBindLoading,

      onClickBack(){
        router.push(`/user/account`)
      },

      validateMobile(val){
        return (/^([0-9]{11})$/.test(val))
      },

      onSubmit () {
        flagBindLoading.value = true
        authApi.updateAccountMobile({ 
          mobile: mobile.value,
          verificationCode: verificationCode.value,
          verificationCodeToken: verificationCodeToken.value,
         }).then(resp => {
           router.push(`/user/account`)
         }, resp => {
           qUtil.notifyError(resp.message)
         }).finally(()=>{
           flagBindLoading.value = false
         })
      },

      onReset () {
        mobile.value = null
        verificationCode.value = null
        verificationCodeToken.value = null
      },
    }
  },
}
</script>