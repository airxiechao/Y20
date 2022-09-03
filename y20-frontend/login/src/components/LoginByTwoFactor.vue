<template>
  <q-form
    @submit="onSubmit"
    class="q-gutter-md q-mt-md"
    autofocus
  >
    <div>
      <q-icon class="vertical-middle q-mr-xs text-green" name="verified_user"/>
      <span class="vertical-middle">两步验证已开启</span>
    </div>
    <q-input
      outlined
      v-model="twoFactorCode"
      label="动态代码"
      hint="请输入身份验证器的动态代码"
      lazy-rules
      :rules="[ 
          val => val && val.length > 0 || '请输入身份验证器的动态代码',
          val => val.length < 50 || '动态代码长度不超过50个字符',
      ]"
    />

    <div class="q-mt-md">
      <q-btn unelevated class="full-width" size="lg" label="登录" :loading="flagLoginLoading" type="submit" color="primary"/>
    </div>

  </q-form>
</template>

<script>
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

    const twoFactorCode = ref(null)
    const flagLoginLoading = ref(false)

    const pathBeforeLogin = computed(() => {
      return store.state['pathBeforeLogin']
    })

    const twoFactorToken = computed(() => {
      return store.state.login.twoFactorToken
    })

    return {
      twoFactorCode,
      flagLoginLoading,

      onSubmit(){
        if(!twoFactorToken.value){
          qUtil.notifyError('两步验证令牌无效')
          return
        }

        flagLoginLoading.value = true
        authApi.loginByTwoFactor({
          twoFactorToken: twoFactorToken.value,
          twoFactorCode: twoFactorCode.value,
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
          qUtil.notifyError('动态代码无效')
        }).finally(() => {
          flagLoginLoading.value = false
        })
      },
    }
  }
}
</script>