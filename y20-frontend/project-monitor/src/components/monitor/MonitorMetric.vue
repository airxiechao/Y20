<template>
  <LayoutTwoColumn class="monitor-metric">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="监视" :to="`/project/${projectId}/monitor`" />
          </q-toolbar>
        </div>

        <div class="q-pa-sm page-content">
          <q-card flat class="bg-white q-pa-md">
            <div class="row q-pb-md ">
              <q-skeleton v-if="!monitor.name" type="text" animation="fade" style="max-width: 150px;" />
              <div v-else class="col page-heading">监视状态 - {{monitor.name}}</div>
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
                <LineChart title="CPU使用率" :data="cpuUsage"/>
                <LineChart title="内存使用量(M)" :data="memoryUsage"/>
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
.monitor-metric{

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
import LineChart from '@/components/monitor/chart/LineChart'

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

    const { projectId, monitorId } = route.params
    const loading = ref(false)

    const monitor = ref({})
    const cpuUsage = ref([])
    const memoryUsage = ref([])

    const load = () => {
      loading.value = true
      monitorApi.get({ monitorId: monitorId }).then(resp => {
        monitor.value = resp.data
      })

      monitorApi.listMonitorMetric({
        monitorId
      }).then(resp => {
        cpuUsage.value = (resp.data['cpuUsage'] || []).map(x => [x.timestamp, x.value])
        memoryUsage.value = (resp.data['memoryBytes'] || []).map(x => [x.timestamp, x.value / 1024 / 1024])
      }, resp => {
        qUtil.notifyError(resp.message || '读取监视状态发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      load();
    })

    return {
      projectId,
      
      monitor,
      loading,

      cpuUsage,
      memoryUsage,

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