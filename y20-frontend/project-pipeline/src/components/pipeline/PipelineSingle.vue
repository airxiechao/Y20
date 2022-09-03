<template>
  <div class="pipeline-single">
    <router-view />
  </div>
</template>

<style lang="scss">
.pipeline-single{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { ref, inject, computed, provide, onMounted } from 'vue'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
  },
  setup(){
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const pipelineName = ref('')

    provide('pipelineName', pipelineName)
    
    const { projectId, pipelineId } = route.params

    onMounted(() => {
      pipelineApi.getBasic({ pipelineId }).then(resp => {
        pipelineName.value = resp.data.name

        store.commit('setTitle', { title: pipelineName.value })
        store.commit('project/pipeline/setPipeline', resp.data)
      })
    })

    return {
      
    }
  }
};
</script>