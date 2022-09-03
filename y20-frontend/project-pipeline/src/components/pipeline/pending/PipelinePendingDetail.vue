<template>
  <div class="pipeline-pending-detail">
    <div class="row q-pb-md">
      <div class="col page-heading text-primary self-center">等待执行</div>
      <div class="self-center">
        <q-btn flat icon="subdirectory_arrow_left" label="返回" :to="`/project/${projectId}/pipeline/${pipelineId}/pending`" />  
      </div>
    </div>
    <q-form class="q-gutter-md">
      <q-skeleton v-if="loading" type="input" animation="fade" />
      <q-input v-else readonly outlined v-model="pending.name" 
        label="执行名称 *"
      />

      <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" :loading="loading" :readonly="true" />
    </q-form>
  </div>
</template>

<style lang="scss">
.pipeline-pending-detail{
  
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
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { ref, inject, computed, onMounted } from 'vue'
import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'
import pipelineApi from "@/api/pipeline.api"

export default {
  components: {
    PipelineInVariablesForm,
  },
  setup(){
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, pipelineId, pipelinePendingId } = route.params
    const loading = ref(false)

    const pending = ref({})
    const pipelineInVariables = ref([])

    const inParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })

    onMounted(() => {
      loading.value = true
      pipelineApi.listVariable({ pipelineId }).then(resp => {
        pipelineInVariables.value = Object.values(resp.data).sort((a,b) => (a.order || 0) - (b.order || 0)).filter(v => v.kind == 'IN')
        pipelineInVariables.value.forEach(v => {
          v.value = v.defaultValue || null
        })

        pipelineApi.getPending({ pipelineId, pipelinePendingId }).then((resp) => {
          pending.value = resp.data
          const pendingInParams = resp.data.inParams
          if(pendingInParams){
            Object.keys(pendingInParams).forEach(k => {
              const i = pipelineInVariables.value.findIndex(v => v.name === k)
              if(i >= 0){
                pipelineInVariables.value[i].value = pendingInParams[k]
              }
            })
          }
          
          loading.value = false
        }, resp => {

        })
      }).finally(() => {

      })
    })

    return {
      dayjs,
      projectId,
      projectName,
      pipelineId,

      pending,
      pipelineInVariables,
      inParams,
      loading,

    }
  }
};
</script>