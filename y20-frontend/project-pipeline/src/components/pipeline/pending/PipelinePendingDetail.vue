<template>
  <div class="pipeline-pending-detail">
    <div class="page-heading q-pb-md">等待执行</div>
    <q-form class="q-gutter-md">
      <q-skeleton v-if="loading" type="input" animation="fade" />
      <q-input v-else readonly outlined v-model="pending.name" 
        label="执行名称 *"
      />

      <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" :loading="loading" :readonly="true" />
    
      <div class="q-pt-sm">
        <q-btn flat class="q-ml-sm bg-grey-2" label="返回" @click="onClickCancel" />
      </div>
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

      onClickCancel() {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/pending`);
      },
    }
  }
};
</script>