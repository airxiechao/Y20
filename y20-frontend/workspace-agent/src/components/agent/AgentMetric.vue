<template>
  <LayoutTwoColumn class="agent-metric">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="节点" to="/workspace/agent" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="bg-white q-pa-md">
            <div class="row q-pb-md ">
              <div class="col page-heading">节点状态 - {{agentId}}</div>
              <div class="self-center">
                <q-btn
                  flat 
                  dense 
                  icon="refresh"
                  label="刷新"
                  :loading="loading"
                  @click="onClickReload"
                />
              </div>
            </div>
            <div class="row full-width">
              <div class="col relative-position" >
                <q-inner-loading :showing="loading" class="z-top">
                  <q-spinner-gears size="38px" />
                </q-inner-loading>
                <LineChart title="CPU使用率" :data="cpuLoad"/>
                <LineChart title="内存使用率" :data="memoryLoad"/>
                <LineChart v-for="(data, title) in filesystemLoad" :key="title" :title="title" :data="data" />
              </div>
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.agent-metric{

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
import LineChart from '@/components/agent/chart/LineChart'

import monitorApi from '@/api/monitor.api'

export default {
  components: {
    LayoutTwoColumn,
    LineChart
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId, agentId } = route.params
    const loading = ref(false)

    const cpuLoad = ref([])
    const memoryLoad = ref([])
    const filesystemLoad = ref({})

    const load = () => {
      loading.value = true
      monitorApi.listAgentMetric({
        agentId
      }).then(resp => {
        cpuLoad.value = (resp.data['cpuLoad'] || []).map(x => [x.timestamp, x.value])
        memoryLoad.value = (resp.data['memoryLoad'] || []).map(x => [x.timestamp, x.value])
        
        const fsKeys = Object.keys(resp.data).filter(x => x.startsWith("filesystem:"))
        fsKeys.forEach(fsKey => {
          let title = fsKey.substring("filesystem:".length);
          title = `磁盘使用率 ${title}`;
          filesystemLoad.value[title] = (resp.data[fsKey] || []).map(x => [x.timestamp, x.value]);
        });
      }, resp => {
        qUtil.notifyError(resp.message || '读取节点状态发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      load();
    })

    return {
      agentId,
      loading,

      cpuLoad,
      memoryLoad,
      filesystemLoad,

      onClickBack(){
        router.back();
      },

      onClickReload(){
        load();
      },
    }
  }
};
</script>