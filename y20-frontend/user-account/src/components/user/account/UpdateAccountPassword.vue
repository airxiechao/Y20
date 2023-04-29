<template>
  <LayoutTwoColumn class="update-account-password">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="账号" to="/user/account" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">修改密码</div>
            <q-form
              @submit="onSubmit"
              @reset="onReset"
              class="q-gutter-md"
            >
              <q-input
                outlined
                type="password"
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
                lazy-rules
                :rules="[
                  val => val !== null && val !== '' || $t('error-no-password2'),
                  val => val === password || $t('error-not-match-password'),
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
                  <BtnSendSmsVerificationCode :validate="false" />
                </template>
              </q-input>

              <div>
                <q-btn unelevated :label="$t('label-bind')" :loading="flagLoading" type="submit" color="primary"/>
                <q-btn flat class="q-ml-sm bg-grey-2" :label="$t('label-cancel')" @click="onClickBack" />
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
    "label-password": "设置密码",
    "error-no-password": "请输入密码",
    "error-password-length": "密码长度为6~50个字符",
    "label-password2": "再次输入密码",
    "error-no-password2": "请再次输入密码",
    "error-not-match-password": "两次输入的密码不一致",
    "label-verification-code": "手机验证码",
    "error-no-verification-code": "请输入手机验证码",
    "label-send-verification-code": "发送验证码",
    "label-bind": "修改",
    "label-reset": "重置",
    "label-cancel": "取消",
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
    
    const password = ref(null)
    const password2 = ref(null)
    const verificationCodeToken = ref(null)
    const verificationCode = ref(null)
    const flagLoading = ref(false)

    provide('verificationCodeToken', verificationCodeToken)

    return {
      password,
      password2,
      verificationCodeToken,
      verificationCode,
      flagLoading,

      onClickBack(){
        router.push(`/user/account`);
      },

      onSubmit () {
        flagLoading.value = true
        authApi.updateAccountPassword({ 
          password: password.value,
          verificationCode: verificationCode.value,
          verificationCodeToken: verificationCodeToken.value,
         }).then(resp => {
           router.push(`/user/account`)
         }, resp => {
           qUtil.notifyError(resp.message)
         }).finally(()=>{
           flagLoading.value = false
         })
      },

      onReset () {
        password.value = null
        password2.value = null
        verificationCode.value = null
        verificationCodeToken.value = null
      },
    }
  },
}
</script>