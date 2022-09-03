<template>
  <LayoutTwoColumn class="disable-account-two-factor">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="账号" to="/user/account" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">关闭两步验证</div>
              <q-form
                autofocus
                @submit="onSubmitDisable"
              >
                <div>
                  <q-input
                    v-model="code"
                    outlined
                    placeholder="动态代码"
                    hint="请输入身份验证器的动态代码"
                    lazy-rules
                    :rules="[
                      val => !!val || '请输入身份验证器的动态代码',
                    ]"
                  />
                </div>
                <div class="q-mt-md">
                  <q-btn unelevated type="submit" class="q-mr-sm" color="primary" label="关闭两步验证" :loading="disableLoading" />
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

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, provide, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const disableLoading = ref(false)
    const code = ref(null)

    return {
      disableLoading,
      code,

      onClickBack(){
        router.push(`/user/account`)
      },

      onSubmitDisable(){
        disableLoading.value = true
        authApi.disableAccountTwoFactor({
          code: code.value
        }).then(resp => {
          qUtil.notifySuccess('两步验证关闭完成')
          router.push(`/user/account`)
        }, resp => {
          qUtil.notifyError('动态代码无效')
        }).finally(() => {
          disableLoading.value = false
        })
      }
      
    }
  },
}
</script>