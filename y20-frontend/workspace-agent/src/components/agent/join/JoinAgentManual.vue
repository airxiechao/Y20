<template>
  <div class="join-agent-manual">
    <q-timeline color="primary">
      <q-timeline-entry
        title="1. 下载 y20-agent-client.zip，解压到安装目录"
      >
        <div>
          <div class="q-pt-sm"><a :href="downloadWinUrl">Windows 下载</a></div>
          <div class="text-grey">支持：Windows 64位</div>
          <div class="q-pt-sm"><a :href="downloadLinuxUrl">Linux 下载</a></div>
          <div class="text-grey">支持：Linux 64位，依赖：systemd, unzip</div>
        </div>
      </q-timeline-entry>

      <q-timeline-entry
        title="2. 修改配置文件 conf/agent-client.yml"
      >
        <q-list>
          <q-item>
            <q-item-section>
              <q-item-label>agentId</q-item-label>
              <q-item-label caption lines="2">必填，自定义的节点唯一标识</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>accessToken</q-item-label>
              <q-item-label caption lines="2">必填，节点访问令牌，在【用户 - 令牌】页面创建</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>serverHost</q-item-label>
              <q-item-label caption lines="2">必填，节点服务器地址：{{window.location.hostname}}</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>serverRpcPort</q-item-label>
              <q-item-label caption lines="2">必填，节点服务器Rpc端口：9100</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>serverRestPort</q-item-label>
              <q-item-label caption lines="2">必填，节点服务器Rest端口：{{window.location.port || (window.location.protocol.startsWith('https') ? '443' : '80')}}</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>serverRestUseSsl</q-item-label>
              <q-item-label caption lines="2">必填，节点服务器Rest是否使用SSL：{{window.location.protocol.startsWith('https')}}</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />

          <q-item>
            <q-item-section>
              <q-item-label>dataDir</q-item-label>
              <q-item-label caption lines="2">必填，节点的本地工作目录，用于存放流水线执行过程中产生的文件：.</q-item-label>
            </q-item-section>
          </q-item>

          <q-separator spaced inset />
        
        </q-list>
      </q-timeline-entry>

      <q-timeline-entry
        title="3. 安装、启动服务"
      >
        <span>以管理员身份运行 install-y20-agent-client-service</span>
      </q-timeline-entry>

      <q-timeline-entry
        title="4. 停止、卸载服务"
      >
        <span>以管理员身份运行 uninstall-y20-agent-client-service</span>
      </q-timeline-entry>

    </q-timeline>
  </div>
</template>

<style lang="scss">
.join-agent-manual{
  
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
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'

import agentApi from '@/api/agent.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId } = route.params

    const latestVersion = ref(null)
    const releaseTime = ref(null)
    const downloadWinUrl = ref(null)
    const downloadLinuxUrl = ref(null)

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
      dayjs,

      latestVersion,
      releaseTime,
      downloadWinUrl,
      downloadLinuxUrl,

    }
  }
};
</script>