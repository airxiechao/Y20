<template>
  <LayoutTwoColumn class="token-create">
    <template v-slot:center>
      <q-dialog persistent v-model="flagShowDialog">
        <q-card>
          <q-card-section>
            <div class="text-h6 text-weight-bold">新令牌</div>
            <div class="text-red">只显示一次，请确保复制并保存</div>
          </q-card-section>

          <q-card-section class="bg-grey-2" style="word-break: break-all;">
            {{ accessToken }}
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="关闭" v-close-popup to="/user/token" />
            <q-btn unelevated label="复制" color="primary" @click="onClickCopyToken" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="令牌" to="/user/token" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md bg-white">
            <div class="q-pb-md">创建新令牌</div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="name"
                :label="`${$t('label-name')} *`"
                :hint="$t('error-name-too-long')"
                lazy-rules
                :rules="[ 
                    val => val && val.length > 0 || $t('error-no-name'),
                    val => val.length < 20 || $t('error-name-too-long')
                ]"
              />

              <q-select
                outlined
                v-model="scope"
                :options="['AGENT', 'WEBHOOK']"
                :label="`${$t('label-scope')} *`"
                :hint="$t('error-no-scope')"
                lazy-rules
                :rules="[ 
                    val => !!val || $t('error-no-scope'),
                ]"
              />

              <q-input outlined v-model="beginTime" 
                :label="`${$t('label-beginTime')} *`"
                lazy-rules
                :rules="[ 
                    val => !!val || $t('error-no-beginTime'),
                    val => validateTime(val) || $t('error-wrong-time-format')
                ]"
              >
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy transition-show="scale" transition-hide="scale">
                      <q-date v-model="beginTime" mask="YYYY-MM-DD HH:mm:ss">
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup label="关闭" color="primary" flat />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>

                <template v-slot:append>
                  <q-icon name="access_time" class="cursor-pointer">
                    <q-popup-proxy transition-show="scale" transition-hide="scale">
                      <q-time v-model="beginTime" mask="YYYY-MM-DD HH:mm:ss" format24h with-seconds>
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup label="关闭" color="primary" flat />
                        </div>
                      </q-time>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <q-input outlined v-model="endTime" 
                :label="`${$t('label-endTime')} *`"
                lazy-rules
                :rules="[ 
                    val => !!val || $t('error-no-endTime'),
                    val => validateTime(val) || $t('error-wrong-time-format')
                ]"
              >
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy transition-show="scale" transition-hide="scale">
                      <q-date v-model="endTime" mask="YYYY-MM-DD HH:mm:ss">
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup label="关闭" color="primary" flat />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>

                <template v-slot:append>
                  <q-icon name="access_time" class="cursor-pointer">
                    <q-popup-proxy transition-show="scale" transition-hide="scale">
                      <q-time v-model="endTime" mask="YYYY-MM-DD HH:mm:ss" format24h with-seconds>
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup label="关闭" color="primary" flat />
                        </div>
                      </q-time>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <div>
                <q-btn unelevated :label="$t('label-create')" :loading="flagCreateLoading" type="submit" color="primary"/>
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

<style lang="scss">
.token-create{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "名称",
    "error-no-name": "请输入令牌名称",
    "error-name-too-long": "名称长度不超过50个字符",
    "label-scope": "使用范围",
    "error-no-scope": "请选择使用范围。AGETN 适用于节点，WEBHOOK 适用于网络钩子",
    "label-beginTime": "开始时间",
    "error-no-beginTime": "请输入开始时间",
    "label-endTime": "结束时间",
    "error-no-endTime": "请输入结束时间",
    "error-wrong-time-format": "时间格式为：YYYY-MM-DD HH:mm:ss",
    "label-create": "创建",
  }
}
</i18n>

<script>
import { copyToClipboard } from 'quasar'
import { LayoutTwoColumn } from 'common'
import { ref, inject } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const name = ref(null)
    const scope = ref(null)
    const beginTime = ref(null)
    const endTime = ref(null)
    const flagShowDialog = ref(false)
    const accessToken = ref(null)

    return {
      name,
      scope,
      beginTime,
      endTime,
      flagShowDialog,
      accessToken,

      validateTime(val){
        return /^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$/.test(val)
      },

      onSubmit(){
        let createFn
        switch(scope.value){
          case 'AGENT':
            createFn = authApi.createAgentAccessToken
            break;
          case 'WEBHOOK':
            createFn = authApi.createWebhookAccessToken
            break;
          default:
            return
        }
        createFn({
          name: name.value,
          scope: scope.value,
          beginTime: beginTime.value,
          endTime: endTime.value,
        }).then(resp => {
          accessToken.value = resp.data
          flagShowDialog.value = true
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      },
      
      onClickCopyToken(){
        copyToClipboard(accessToken.value).then(() => {
          qUtil.notifySuccess('已复制到粘贴板')
        }).catch(() => {
          qUtil.notifyError('复制到粘贴板发生错误')
        })
      },
    }
  }
};
</script>