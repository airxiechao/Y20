<template>
  <LayoutTwoColumn class="account-detail">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <div class="page-heading">账号</div>
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="account-detail-card">
            <q-list padding>
              <q-item>
                <q-item-section avatar>
                  <q-icon name="person" size="md" />
                </q-item-section>

                <q-item-section>
                  <q-item-label caption lines="1">用户名</q-item-label>
                  <q-item-label>{{account.username}}</q-item-label>
                </q-item-section>
              </q-item>

              <q-separator spaced />

              <q-item>
                <q-item-section avatar>
                  <q-icon name="password" size="md" />
                </q-item-section>

                <q-item-section>
                  <q-item-label caption lines="1">密码</q-item-label>
                  <q-item-label>******</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-btn flat round icon="edit" @click="onClickUpdatePassword" />
                </q-item-section>

              </q-item>

              <q-separator spaced />

              <q-item>
                <q-item-section avatar>
                  <q-icon name="phone_android" size="md" />
                </q-item-section>

                <q-item-section>
                  <q-item-label caption lines="1">手机</q-item-label>
                  <q-item-label>{{account.mobile || '未绑定'}}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-btn flat round icon="edit" @click="onClickUpdateMobile" />
                </q-item-section>

              </q-item>
              
              <q-separator spaced />

              <q-item>
                <q-item-section avatar>
                  <q-icon name="email" size="md" />
                </q-item-section>

                <q-item-section>
                  <q-item-label caption lines="1">邮箱</q-item-label>
                  <q-item-label>{{account.email || '未绑定'}}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-btn flat round icon="edit" @click="onClickUpdateEmail" />
                </q-item-section>

              </q-item>

              <q-separator spaced />

              <q-item>
                <q-item-section avatar>
                  <q-icon name="verified_user" size="md" />
                </q-item-section>

                <q-item-section>
                  <q-item-label caption lines="1">两步验证</q-item-label>
                  <q-item-label>{{account.flagTwoFactor ? '已开启' : '未开启'}}</q-item-label>
                </q-item-section>

                <q-item-section side>
                   <q-toggle :model-value="!!account.flagTwoFactor" @click="onClickUpdateTwoFactor" />
                </q-item-section>

              </q-item>

            </q-list>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss" scoped>
.account-detail{
  &-card{
  }
}
</style>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const account = ref({})

    onMounted(() => {
      authApi.getAccount().then(resp => {
        account.value = resp.data
      })
    })

    return {
      account,

      onClickUpdatePassword(){
        router.push(`/user/account/password`)
      },

      onClickUpdateMobile(){
        router.push(`/user/account/mobile`)
      },

      onClickUpdateEmail(){
        router.push(`/user/account/email`)
      },

      onClickUpdateTwoFactor(){
        if(account.value.flagTwoFactor){
          router.push(`/user/account/twofactor/disable`)
        }else{
          router.push(`/user/account/twofactor/enable`)
        }
      },
    }
  },
}
</script>