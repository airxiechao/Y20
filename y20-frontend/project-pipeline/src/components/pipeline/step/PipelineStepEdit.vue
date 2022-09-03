<template>
  <div class="pipeline-step-edit">
    <component :is="stepTypeComponent"></component>
  </div>
</template>

<style lang="scss">
.pipeline-step-edit{
  
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
import pipelineApi from '@/api/pipeline.api'
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
    const { projectId, pipelineId, stepPosition } = route.params
    const step = ref(null)
    provide('step', step)
    const stepTypeComponent = computed(() => {
      if(step.value){
        if(step.value.type == 'remote-prepare-env'){
          return StepTypeEnv
        }else if(step.value.type == 'remote-call-pipeline'){
          return StepTypeCallPipeline
        }else{
          return StepTypeNormal
        }
      }
    })
    
    onMounted(() => {
      pipelineApi.getStep({ pipelineId, position: stepPosition }).then(resp => {
        step.value = resp.data
      })
    })

    return {
      pipelineName,
      step,
      stepTypeComponent,
    }
  }
};
</script>