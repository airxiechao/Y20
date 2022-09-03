<template>
  <LayoutTwoColumn class="update-account-email">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="账号" to="/user/account" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">绑定新邮箱</div>
            <q-form
              @submit="onSubmit"
              @reset="onReset"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="email"
                :label="`${$t('label-email')} *`"
                lazy-rules
                :rules="[
                  val => val !== null && val !== '' || $t('error-no-email'),
                  val => validateEmail(val) || $t('error-email-not-valid'),
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
                  <BtnSendEmailVerificationCode />
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
    "label-email": "邮箱",
    "error-no-email": "请输入邮箱",
    "error-email-not-valid": "请输入有效的邮箱",
    "label-verification-code": "邮箱验证码",
    "error-no-verification-code": "请输入邮箱验证码",
    "label-send-verification-code": "发送验证码",
    "label-bind": "绑定",
    "label-reset": "重置",
  },
}
</i18n>


<script>
import { LayoutTwoColumn, BtnSendEmailVerificationCode } from 'common'
import { ref, inject, provide, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
    BtnSendEmailVerificationCode,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const email = ref(null)
    const verificationCodeToken = ref(null)
    const verificationCode = ref(null)
    const flagBindLoading = ref(false)

    provide('email', email)
    provide('verificationCodeToken', verificationCodeToken)

    return {
      email,
      verificationCodeToken,
      verificationCode,
      flagBindLoading,

      onClickBack(){
        router.push(`/user/account`)
      },

      validateEmail(val){
        return (/^[^@]+@[^@]+$/.test(val))
      },

      onSubmit () {
        flagBindLoading.value = true
        authApi.updateAccountEmail({ 
          email: email.value,
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
        email.value = null
        verificationCode.value = null
        verificationCodeToken.value = null
      },
    }
  },
}
</script>