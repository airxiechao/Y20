<template>
  <div class="pipeline-step-new">
    <component :is="stepTypeComponent"></component>
  </div>
</template>

<style lang="scss">
.pipeline-step-new{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { ref, inject, provide, computed, onMounted } from 'vue'
import StepTypeEnv from './StepTypeEnv'
import StepTypeNormal from './StepTypeNormal'
import StepTypeCallPipeline from './StepTypeCallPipeline'

export default {
  components: {
    StepTypeEnv,
    StepTypeNormal,
    StepTypeCallPipeline,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, stepPosition, stepType } = route.params
    const step = ref(null)
    provide('step', step)
    const stepTypeComponent = computed(() => {
      if(stepType == 'remote-prepare-env'){
        return StepTypeEnv
      }else if(stepType == 'remote-call-pipeline'){
        return StepTypeCallPipeline
      }else{
        return StepTypeNormal
      }
    })

    return {
      pipelineName,
      step,
      stepTypeComponent,
    }
  }
};
</script>