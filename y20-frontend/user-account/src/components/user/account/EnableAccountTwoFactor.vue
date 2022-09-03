<template>
  <LayoutTwoColumn class="enable-account-two-factor">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="账号" to="/user/account" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">开启两步验证</div>
            <q-stepper
              v-model="step"
              :vertical="$q.screen.lt.sm"
              color="primary"
              animated
            >
              <q-step
                :name="1"
                title="安装身份验证器"
                icon="get_app"
                :done="step > 1"
                :header-nav="step > 1"
              >
                <div>
                  1. 在手机应用市场中搜索 “Authenticator”，下载安装身份验证器
                </div>
                <q-list bordered class="q-mt-md" style="max-width: 280px">
                  <q-item>
                    <q-item-section avatar>
                      <img 
                        alt="fig-authenticator" 
                        :src="`/${FRONTEND_SERVICE_NAME}/static/img/icon-authenticator.jpg`"
                        style="width: 50px"
                      />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label>Authenticator</q-item-label>
                      <q-item-label caption>开发商：微软（中国）有限公司</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
                <q-stepper-navigation>
                  <q-btn unelevated color="primary" label="下一步" @click="goto(2)" />
                </q-stepper-navigation>
              </q-step>

              <q-step
                :name="2"
                title="添加帐户"
                icon="person"
                :done="step > 2"
                :header-nav="step > 2"
              >
                <div>
                  2. 打开身份验证器，在右上角弹出菜单中点击 “添加帐户”，选择 “其他帐户” 类型，扫描下方二维码，添加帐户
                </div>
                <template v-if="secretLoading">
                  <q-skeleton class="q-my-md" width="100px" height="100px" square />
                  <q-skeleton type="text" width="50px" />
                  <q-skeleton type="text" width="100px" />
                  <q-skeleton type="text" width="100px"/>
                </template>
                <template v-else>
                  <div class="q-my-md">
                    <div ref="secretQr"></div>
                  </div>
                  <div>
                    如果无法扫码，请手动输入：
                  </div>
                  <q-list bordered dense class="q-mt-sm" style="max-width: 400px;">
                    <q-item>
                      <q-item-section style="min-width: 35px;">帐户</q-item-section>
                      <q-item-section side style="word-break: break-all;">{{username}}</q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section style="min-width: 35px;">密钥</q-item-section>
                      <q-item-section side style="word-break: break-all;">{{secret}}</q-item-section>
                    </q-item>
                  </q-list>
                </template>
                
                <q-stepper-navigation>
                  <q-btn unelevated class="q-mr-sm" color="primary" label="下一步" @click="goto(3)" />
                  <q-btn flat color="primary" label="返回" @click="goto(1)" />
                </q-stepper-navigation>
              </q-step>

              <q-step
                :name="3"
                title="输入动态代码"
                icon="password"
                :header-nav="step > 3"
              >
                <div>
                  3. 在身份验证器中，进入帐户，输入屏幕上的动态代码
                </div>
                <q-form
                  class="q-mt-md"
                  autofocus
                  @submit="onSubmitEnable"
                >
                  <div>
                    <q-input
                      v-model="code"
                      outlined
                      dense
                      style="width: 150px"
                      placeholder="动态代码"
                      lazy-rules
                      :rules="[
                        val => !!val || '请输入动态代码',
                      ]"
                    />
                  </div>

                  <q-stepper-navigation>
                    <q-btn unelevated type="submit" class="q-mr-sm" color="primary" label="开启两步验证" :loading="enableLoading" />
                    <q-btn flat color="primary" label="返回" @click="goto(2)" />
                  </q-stepper-navigation>
                </q-form>
              </q-step>
            </q-stepper>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.enable-account-two-factor{
  .q-stepper__header--border{
    border-bottom: 0;
  }
}
</style>

<script>
import QRCode from 'qrcodejs2'
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

    const secretLoading = ref(false)
    const enableLoading = ref(false)
    const step = ref(1)

    const secretQr = ref(null)
    const username = ref(null)
    const secret = ref(null)
    const code = ref(null)

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,

      secretLoading,
      enableLoading,
      step,

      secretQr,
      username,
      secret,
      code,

      onClickBack(){
        router.push(`/user/account`)
      },

      onSubmitEnable(){
        enableLoading.value = true
        authApi.enableAccountTwoFactor({
          code: code.value
        }).then(resp => {
          qUtil.notifySuccess('两步验证开启完成')
          router.push(`/user/account`)
        }, resp => {
          qUtil.notifyError('动态代码无效')
        }).finally(() => {
          enableLoading.value = false
        })
      },

      goto(i){
        step.value = i

        if(i == 2){
          const renderQr = () => {
            setTimeout(() => {
              new QRCode(secretQr.value, {
                text: `otpauth://totp/${username.value}?issuer=Y20&secret=${secret.value}`,
                width: 100,
                height: 100,
                colorDark : "#000000",
                colorLight : "#ffffff",
                correctLevel : QRCode.CorrectLevel.H
              }) 
            }, 1)
          }

          if(!secret.value){
            secretLoading.value = true
            authApi.createAccountTwoFactorSecret().then(resp => {
              const data = resp.data
              username.value = data.username
              secret.value = data.secret

              renderQr()

            }, resp => {
              qUtil.notifyError('生成两步验证密钥发生错误')
            }).finally(() => {
              secretLoading.value = false
            })
          }else{
            renderQr()
          }
          
        }
      },
    }
  },
}
</script>