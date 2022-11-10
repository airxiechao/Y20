<template>
  <div class="join-agent-script">
    <div class="q-mb-lg">
      <div>
        <div class="text-primary q-pb-md">操作系统</div>
        <q-btn-toggle
          v-model="osType"
          no-caps
          toggle-color="primary"
          :options="[
            {label: 'Windows', value: 'WINDOWS'},
            {label: 'Linux', value: 'LINUX'},
          ]"
        />
      </div>
      <div v-if="osType == 'WINDOWS'" class="text-grey q-mt-sm">
        支持：Windows 64位
      </div>
      <div v-else-if="osType == 'LINUX'" class="text-grey q-mt-sm">
        支持：Linux 64位，依赖：systemd, unzip
      </div>
    </div>
    <div class="text-primary q-pb-md">节点配置</div>
    <q-form
      @submit="onSubmit"
      class="q-gutter-lg q-mb-md"
    >
      <q-input
        outlined
        v-model="agentId"
        label="唯一标识 *"
        hint="agentId（必填），自定义的节点唯一标识"
        lazy-rules
        :rules="[ val => val && val.length > 0 || '请输入节点唯一标识']"
      />

      <q-input
        outlined
        v-model="accessToken"
        label="访问令牌 *"
        hint="accessToken（必填），节点访问令牌，在【用户 - 令牌】页面创建。自动生成的令牌有效期一年"
        lazy-rules
        :rules="[ val => val && val.length > 0 || '请输入节点访问令牌']"
      >
        <template v-slot:append>
          <q-btn 
            class="bg-white"
            :disable="!agentId"
            icon="auto_fix_high"
            label="自动生成"
            @click="onClickGenerateAccessToken"
          />
        </template>
      </q-input>

      <q-input
        outlined
        v-model="serverHost"
        label="服务器地址 *"
        :hint="`serverHost（必填），节点服务器地址：${window.location.hostname}`"
        lazy-rules
        :rules="[ val => val && val.length > 0 || '请输入节点服务器地址']"
      />

      <q-input
        outlined
        v-model="serverRpcPort"
        label="服务器Rpc端口 *"
        hint="serverRpcPort（必填），节点服务器Rpc端口：9100"
        lazy-rules
        :rules="[ val => !!val || '请输入服务器Rpc端口']"
      />

      <q-input
        outlined
        v-model="serverRestPort"
        label="服务器Rest端口 *"
        :hint="`serverRestPort（必填），节点服务器Rest端口：${window.location.port || (window.location.protocol.startsWith('https') ? '443' : '80')}`"
        lazy-rules
        :rules="[ val => !!val || '请输入服务器Rest端口']"
      />

      <q-select
        outlined
        v-model="serverRestUseSsl"
        :options="[
          {
            label: '是', value: true,
          },
          {
            label: '否', value: false,
          },
        ]" 
        label="服务器Rest是否使用SSL *"
        :hint="`serverRestUseSsl（必填），节点服务器Rest是否使用SSL：${window.location.protocol.startsWith('https')}`"
        emit-value 
        map-options
      />

      <q-input
        outlined
        v-model="dataDir"
        label="工作目录 *"
        hint="dataDir（必填），节点的本地工作目录，用于存放流水线执行过程中产生的文件：."
        lazy-rules
        :rules="[ val => val && val.length > 0 || '请输入节点的本地工作目录']"
      />

      <div>
        <q-btn unelevated label="生成接入脚本" type="submit" color="primary" :loading="scriptLoading" />
      </div>
    </q-form>
    <div class="q-mt-md">
      <div v-if="osType == 'WINDOWS'" class="text-warning q-py-xs">
        <q-icon name="warning" />
        注意：Windows 请使用 <span class="text-bold">Powershell</span> 终端，运行脚本</div>
      <q-input
        v-model="script"
        stack-label
        label="接入脚本"
        :hint="`本地打开 ${scriptType} 终端，切换到安装目录，运行脚本，完成接入`"
        outlined
        type="textarea"
      />
    </div>
  </div>
</template>

<style lang="scss">
.join-agent-script{
  
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
import { copyToClipboard } from 'quasar'

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'

import authApi from '@/api/auth.api'
import agentApi from '@/api/agent.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId } = route.params

    const latestVersion = ref(null)
    const releaseTime = ref(null)
    const downloadWinUrl = ref(null)
    const downloadLinuxUrl = ref(null)

    const osType = ref('WINDOWS')
    const script = ref('')
    const agentId = ref('')
    const accessToken = ref('')
    const serverHost = ref(window.location.hostname)
    const serverRpcPort = ref(9100)
    const serverRestPort = ref(window.location.port || (window.location.protocol.startsWith('https') ? '443' : '80'))
    const serverRestUseSsl = ref(window.location.protocol.startsWith('https'))
    const dataDir = ref('.')
    const scriptLoading = ref(false)

    const scriptType = computed(() => {
      switch(osType.value){
        case 'WINDOWS':
          return 'Powershell'
        case 'LINUX':
          return 'Bash'
        default:
          return ''
      }
    })

    onMounted(() => {
      agentApi.getLatestVersion().then(resp => {
        latestVersion.value = resp.data.version
        releaseTime.value = resp.data.releaseTime
        downloadWinUrl.value = resp.data.downloadWinUrl
        downloadLinuxUrl.value = resp.data.downloadLinuxUrl
      }, resp => {
        qUtil.notifyError(resp.message || '获取最新版本发生错误')
      })
    })

    return {
      window: window,
      latestVersion,
      releaseTime,
      downloadWinUrl,
      downloadLinuxUrl,

      osType,
      scriptType,
      script,
      agentId,
      accessToken,
      serverHost,
      serverRpcPort,
      serverRestPort,
      serverRestUseSsl,
      dataDir,
      scriptLoading,

      onClickGenerateAccessToken(){
        const now = dayjs()
        const beginTime = now.format('YYYY-MM-DD HH:mm:ss')
        const endTime = now.add(1, 'year').format('YYYY-MM-DD HH:mm:ss')

        authApi.createAgentAccessToken({
          name: `${agentId.value}-自动生成`,
          beginTime,
          endTime,
        }).then(resp => {
          const token = resp.data
          accessToken.value = token
        }, resp => {
          qUtil.notifyError(resp.message || '自动生成令牌发生错误')
        })
      },

      onSubmit(){
        scriptLoading.value = true
        agentApi.generateJoinScript({
          osType: osType.value,
          agentId: agentId.value,
          accessToken: accessToken.value,
          serverHost: serverHost.value,
          serverRpcPort: serverRpcPort.value,
          serverRestPort: serverRestPort.value,
          serverRestUseSsl: serverRestUseSsl.value,
          dataDir: dataDir.value,
        }).then(resp => {
          script.value = resp.data

          copyToClipboard(script.value).then(() => {
            qUtil.notifySuccess('已复制到粘贴板')
          }).catch(() => {
            qUtil.notifyError('复制到粘贴板发生错误')
          })
        }, resp => {
          qUtil.notifyError('生成接入脚本方式错误')
        }).finally(() => {
          scriptLoading.value = false
        })
      },

    }
  }
};
</script>