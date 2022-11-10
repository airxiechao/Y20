<template>
  <LayoutTwoColumn class="config-agent">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="节点" to="/workspace/agent" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="bg-white q-pa-md">
            <div class="q-pb-md">配置节点 - {{agentId}}</div>
            <template v-if="readLoading">
              <div v-for="i in [1,2]" :key="i" :class="{'q-mb-md': i < 2}">
                <q-skeleton type="input" />
              </div>
            </template>
            <template v-else>
              <q-form
                @submit="onSubmit"
                class="q-gutter-md"
              >
                <q-input
                  outlined
                  v-model="configAgentId"
                  label="唯一标识 *"
                  hint="agentId（必填），自定义的节点唯一标识"
                  lazy-rules
                  :rules="[ val => val && val.length > 0 || '请输入节点唯一标识']"
                />

                <q-input
                  outlined
                  v-model="configAccessToken"
                  label="访问令牌 *"
                  hint="accessToken（必填），节点访问令牌，在【用户 - 令牌】页面创建。自动生成的令牌有效期一年"
                  lazy-rules
                  :rules="[ val => val && val.length > 0 || '请输入节点访问令牌']"
                >
                  <template v-slot:append>
                    <q-btn 
                      class="bg-white"
                      :disable="!configAgentId"
                      icon="auto_fix_high"
                      label="自动生成"
                      @click="onClickGenerateAccessToken"
                    />
                  </template>
                </q-input>
                <q-badge v-if="configAccessTokenName" color="grey">
                  令牌名称：{{configAccessTokenName}}
                </q-badge>
                <q-badge v-if="configAccessTokenEndTime" color="grey">
                  令牌过期时间：{{dayjs(configAccessTokenEndTime).format('YYYY-MM-DD HH:mm:ss')}}
                </q-badge>
                <q-badge v-if="configAccessTokenEndTime && configAccessTokenEndTime <= new Date().getTime()" color="warning">令牌过期</q-badge>
                <q-badge v-else-if="configAccessTokenEndTime && configAccessTokenEndTime - new Date().getTime() < 30*24*60*60*1000" color="warning">即将过期</q-badge>

                <q-input
                  outlined
                  v-model="configServerHost"
                  label="服务器地址 *"
                  hint="serverHost（必填），节点服务器地址"
                  lazy-rules
                  :rules="[ val => val && val.length > 0 || '请输入节点服务器地址']"
                />

                <q-input
                  outlined
                  v-model="configServerRpcPort"
                  label="服务器Rpc端口 *"
                  hint="serverRpcPort（必填），节点服务器Rpc端口"
                  lazy-rules
                  :rules="[ val => !!val || '请输入服务器Rpc端口']"
                />

                <q-input
                  outlined
                  v-model="configServerRestPort"
                  label="服务器Rest端口 *"
                  hint="serverRestPort（必填），节点服务器Rest端口"
                  lazy-rules
                  :rules="[ val => !!val || '请输入服务器Rest端口']"
                />

                <q-select
                  outlined
                  v-model="configServerRestUseSsl"
                  :options="[
                    {
                      label: '是', value: true,
                    },
                    {
                      label: '否', value: false,
                    },
                  ]" 
                  label="服务器Rest是否使用SSL *"
                  hint="serverRestUseSsl（必填），节点服务器Rest是否使用SSL"
                  emit-value 
                  map-options
                />

                <q-input
                  outlined
                  v-model="configDataDir"
                  label="工作目录 *"
                  hint="dataDir（必填），节点的本地工作目录，用于存放流水线执行过程中产生的文件"
                  lazy-rules
                  :rules="[ val => val && val.length > 0 || '请输入节点的本地工作目录']"
                />

                <div>
                  <q-btn unelevated :loading="saveLoading" label="保存并重启服务" type="submit" color="primary"/>
                </div>
              </q-form>
            </template>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.config-agent{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import { ref, inject, computed, onMounted } from 'vue'

import { LayoutTwoColumn } from 'common'
import CodeEditor from '@/components/agent/codeeditor/CodeEditor'

import agentApi from '@/api/agent.api'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
    CodeEditor,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId, agentId } = route.params

    const readLoading = ref(false)
    const saveLoading = ref(false)
    const config = ref('')

    const configAgentId = ref(null)
    const configAccessToken = ref(null)
    const configAccessTokenName = ref(null)
    const configAccessTokenEndTime = ref(null)
    const configServerHost = ref(null)
    const configServerRpcPort = ref(null)
    const configServerRestPort = ref(null)
    const configServerRestUseSsl = ref(null)
    const configDataDir = ref(null)

    const updateConfigField = (conf, field, value) => {
      if(conf.indexOf(`${field}:`) >= 0){
        const regex =  new RegExp(`${field}:(.+)`, 'g');
        conf = conf.replace(regex, `${field}: ${value}`)
      }else{
        conf = conf + (conf.endsWith('\n') ? '' : '\n') + `${field}: ${value}\n`
      }

      return conf
    }

    const newConfig = computed(() => {
      let conf = config.value
      conf = updateConfigField(conf, 'agentId', configAgentId.value)
      conf = updateConfigField(conf, 'accessToken', configAccessToken.value)
      conf = updateConfigField(conf, 'serverHost', configServerHost.value)
      conf = updateConfigField(conf, 'serverRpcPort', configServerRpcPort.value)
      conf = updateConfigField(conf, 'serverRestPort', configServerRestPort.value)
      conf = updateConfigField(conf, 'serverRestUseSsl', configServerRestUseSsl.value)
      conf = updateConfigField(conf, 'dataDir', configDataDir.value)
      return conf
    })

    const extractConfigValue = (name) => {
      const regex = new RegExp(`${name}:(.+)`, 'g')
      const match = regex.exec(config.value)
      if(!match){
        return ''
      }

      return match[1].trim()
    }

    onMounted(() => {
      readLoading.value = true
      agentApi.readConfig({
        agentId
      }).then(resp => {
        config.value = resp.data

        configAgentId.value = extractConfigValue('agentId')
        configAccessToken.value = extractConfigValue('accessToken')
        configServerHost.value = extractConfigValue('serverHost')
        configServerRpcPort.value = extractConfigValue('serverRpcPort')
        configServerRestPort.value = extractConfigValue('serverRestPort')
        configServerRestUseSsl.value = extractConfigValue('serverRestUseSsl') == 'true'
        configDataDir.value = extractConfigValue('dataDir')

        readLoading.value = false

        agentApi.getAgentAccessToken({
          agentAccessToken: configAccessToken.value
        }).then(resp => {
          const accessTokenData = resp.data
          configAccessTokenName.value = accessTokenData.name
          configAccessTokenEndTime.value = accessTokenData.endTime
        })
      }, resp => {
        qUtil.notifyError(resp.message || '读取节点配置发生错误')
      }).finally(() => {
        
      })

    })

    return {
      dayjs,

      readLoading,
      saveLoading,
      
      agentId,
      config,

      configAgentId,
      configAccessToken,
      configAccessTokenName,
      configAccessTokenEndTime,
      configServerHost,
      configServerRpcPort,
      configServerRestPort,
      configServerRestUseSsl,
      configDataDir,

      onClickGenerateAccessToken(){
        const now = dayjs()
        const beginTime = now.format('YYYY-MM-DD HH:mm:ss')
        const endTime = now.add(1, 'year').format('YYYY-MM-DD HH:mm:ss')

        authApi.createAgentAccessToken({
          name: `${configAgentId.value}-自动生成`,
          beginTime,
          endTime,
        }).then(resp => {
          const token = resp.data
          configAccessToken.value = token
        }, resp => {
          qUtil.notifyError(resp.message || '自动生成令牌发生错误')
        })
      },

      onSubmit(){
        saveLoading.value = true
        agentApi.saveConfig({
          agentId,
          config: newConfig.value
        }).then(resp => {
          agentApi.restart({ 
            agentId, 
          }).then(resp => {
            qUtil.notifySuccess('保存完成并重启中')
            router.push('/workspace/agent')
          }, resp => {
            qUtil.notifyError(resp.message || '重启发生错误')
          }).finally(() => {
            saveLoading.value = false
          })
        }, resp => {
          saveLoading.value = false
          qUtil.notifyError(resp.message || '保存发生错误')
        })
      },

    }
  }
};
</script>