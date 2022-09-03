<template>
  <div>
    <component :is="stepTypeComponent"></component>
  </div>
</template>

<style lang="scss">
.template-step{
  
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
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const { templateId, stepPosition } = route.params
    
    const template = inject('template')
    const step = ref(null)
    provide('step', step)

    const stepTypeComponent = computed(() => {
      if(template.value.steps){
        const stepInPos = template.value.steps[stepPosition]
        if(stepInPos){
          step.value = stepInPos
          if(stepInPos.type == 'remote-prepare-env'){
            return StepTypeEnv
          }else if(stepInPos.type == 'remote-call-pipeline'){
            return StepTypeCallPipeline
          }else{
            return StepTypeNormal
          }
        }
      }

    })

    return {
      step,
      stepTypeComponent,

      onClickBack(){
        router.push(`/workspace/template/${templateId}/step`)
      },
    }
  }
}

</script>