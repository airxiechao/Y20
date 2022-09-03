<template>
  <q-form
    @submit="onSubmit"
    class="q-gutter-md"
  >
    <q-input
      outlined
      v-model="username"
      :label="$t('label-username')"
      :hint="$t('error-username-format')"
      lazy-rules
      :rules="[ 
          val => val && val.length > 0 || $t('error-no-username'),
          val => /^[a-zA-Z0-9-_]{3,50}$/.test(val) || $t('error-username-format')
      ]"
    />

    <q-input
      outlined
      type="password"
      v-model="password"
      :label="$t('label-password')"
      :hint="$t('error-password-length')"
      lazy-rules
      :rules="[
        val => val !== null && val !== '' || $t('error-no-password'),
        val => val.length >= 6 && val.length <= 50 || $t('error-password-length'),
      ]"
    />

    <div class="q-mt-md">
      <q-btn unelevated class="full-width" size="lg" :label="$t('label-login')" :loading="flagLoginLoading" type="submit" color="primary"/>
    </div>

  </q-form>
</template>

<i18n>
{
  "zhCN": {
    "label-username": "用户名",
    "error-no-username": "请输入用户名",
    "error-username-format": "用户名为3~50个字符",
    "label-password": "密码",
    "error-no-password": "请输入密码",
    "error-password-length": "密码长度为6~50个字符",
    "label-login": "登录",
  }
}
</i18n>

<script>
import { PAGE_NAME } from '@/page.config.js'
import { ref, inject, computed } from 'vue'
import authApi from '@/api/auth.api'

export default {
  setup(){
    const useStore = inject('useStore')
    const store = useStore()

    const useRouter = inject('useRouter')
    const router = useRouter()

    const useQuasar = inject('useQuasar')
    const $q = useQuasar()
    const qUtil = inject('quasarUtil')($q)

    const username = ref(null)
    const password = ref(null)
    const flagLoginLoading = ref(false)

    const pathBeforeLogin = computed(() => {
      return store.state['pathBeforeLogin']
    })

    return {
      username,
      password,
      flagLoginLoading,

      onSubmit(){
        flagLoginLoading.value = true
        authApi.loginByUsername({
          username: username.value,
          password: password.value,
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
              qUtil.notifyError('用户名或密码错误')
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