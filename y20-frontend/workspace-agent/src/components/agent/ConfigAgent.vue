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
            <q-form
              @submit="onSubmit"
              class="q-gutter-sm"
            >
              <q-field
                borderless
                stack-label
                bg-color="white"
                :loading="readLoading"
                v-model="config"
                label="conf/agent-client.yml"
                lazy-rules
                :rules="[ 
                  val => val && val.length > 0 || '请输入节点配置',
                ]"
              >
                <CodeEditor v-model="config"/>
              </q-field>

              <div>
                <q-btn unelevated :loading="saveLoading" label="保存并重启服务" type="submit" color="primary"/>
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
import { ref, inject, onMounted } from 'vue'

import { LayoutTwoColumn } from 'common'
import CodeEditor from '@/components/agent/codeeditor/CodeEditor'

import agentApi from '@/api/agent.api'

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

    onMounted(() => {
      readLoading.value = true
      agentApi.readConfig({
        agentId
      }).then(resp => {
        config.value = resp.data
      }, resp => {
        qUtil.notifyError(resp.message || '读取节点配置发生错误')
      }).finally(() => {
        readLoading.value = false
      })

    })

    return {
      readLoading,
      saveLoading,
      
      agentId,
      config,

      onSubmit(){
        saveLoading.value = true
        agentApi.saveConfig({
          agentId,
          config: config.value
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