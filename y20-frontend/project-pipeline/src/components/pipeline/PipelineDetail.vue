<template>
  <LayoutTwoColumn class="pipeline-detail">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <div>
            <q-toolbar>
              <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="流水线" :to="`/project/${projectId}/pipeline`" />
              <q-toolbar-title>
              </q-toolbar-title>
              <q-btn flat label="发布" color="primary" :to="`/project/${projectId}/pipeline/${pipelineId}/publish`" />
              <q-btn unelevated label="启动" color="primary" icon="play_arrow" class="q-ml-sm" :to="`/project/${projectId}/pipeline/${pipelineId}/run/create`" />
            </q-toolbar>
          </div>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="pipeline-detail-card">
            <div class="q-pa-md">
              <q-skeleton v-if="!pipelineName" type="text" animation="fade" style="max-width: 150px;" />
              <span v-else>{{pipelineName}}</span>
            </div>
            <q-tabs 
              align="justify" 
              v-model="tab" 
              active-color="primary"
              narrow-indicator
            >
              <q-tab name="run" label="执行" />
              <q-tab name="step" label="步骤" />
              <q-tab name="variable" label="变量" />
              <q-tab name="file" label="文件" />
              <q-tab name="trigger" label="触发" />
              <q-tab name="pending" label="队列" />
              <q-tab name="config" label="设置" />
            </q-tabs>
            <q-separator />
            <div class="q-pa-md">
              <router-view />
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/pipeline-edit.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何编排流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

          <q-item clickable tag="a" href="/docs/guide/pipeline-run.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何运行流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.pipeline-detail{
  &-card{
    
  }
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const projectName = inject('projectName')
    const pipelineName = inject('pipelineName')
    const pathToken = route.path.split('/')
    const tab = ref(
      pathToken.length > 5  ? pathToken[5] : 'run'
    )
    const { projectId, pipelineId } = route.params

    watch(tab, (tab) => {
      router.push(`/project/${projectId}/pipeline/${pipelineId}/${tab}`)
    })

    onMounted(() => {
      store.commit('setTitle', { title: pipelineName.value })
    })

    return {
      projectId,
      projectName,
      pipelineId,
      pipelineName,
      tab,
    }
  }
};
</script>