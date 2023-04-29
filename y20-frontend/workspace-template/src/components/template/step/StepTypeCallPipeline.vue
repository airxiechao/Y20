<template>
  <div class="step-type-call-pipeline" style="position:relative;">
    <div>
      <div class="row q-pb-md page-heading">
        <div class="col">
          <div>步骤类型 - {{ stepTypeName }}</div>
          <q-breadcrumbs class="text-subtitle2 text-grey" active-color="grey">
            <q-breadcrumbs-el :label="`第 ${parseInt(stepPosition)+1} 步`" />
          </q-breadcrumbs>
        </div>
      </div>
      <q-form class="q-gutter-md">
        <q-input readonly outlined label="名称 *" v-model="step.name" :rules="[val => !!val]" />
        <div class="text-primary text-bold">参数</div>
        <q-card v-if="loading" flat class="q-pa-sm">
          <div class="row q-mb-sm" v-for="pi in [1,2,3]" :key="pi">
            <div class="q-pr-sm" style="width: 80px;"><q-skeleton type="text" animation="fade" /></div>
            <div class="col"><q-skeleton type="text" animation="fade" /></div>
          </div>
        </q-card>
        <template v-else>
          <q-list dense bordered separator class="rounded-borders text-grey-6">
            <q-item>
              <q-item-section>
                略
              </q-item-section>
            </q-item>
          </q-list>
        </template>

        <div class="text-primary text-bold q-pt-md">输出</div>
        <q-card v-if="loading" flat class="q-pa-sm">
          <div class="row q-mb-sm" v-for="pi in [1]" :key="pi">
            <div class="q-pr-sm" style="width: 80px;"><q-skeleton type="text" animation="fade" /></div>
            <div class="col"><q-skeleton type="text" animation="fade" /></div>
          </div>
        </q-card>
        <q-list v-else dense bordered separator class="rounded-borders text-grey-6">
          <template v-if="stepTypeOutputs && stepTypeOutputs.length > 0">
            <q-item v-for="(out, i) in stepTypeOutputs" :key="i">
              <q-item-section>
                {{ out }}
              </q-item-section>
            </q-item>
          </template>
          <q-item v-else>
            <q-item-section>
              无
            </q-item-section>
          </q-item>
        </q-list>
      
        <div class="q-pt-sm">
          <q-btn flat class="bg-grey-2" label="返回" @click="onClickBack" />
        </div>
      </q-form>
    </div>
    <!-- <q-inner-loading :showing="loading">
      <q-spinner-gears size="30px" color="primary" />
    </q-inner-loading> -->
  </div>
</template>

<style lang="scss">
.step-type-call-pipeline{
  
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

import { ref, inject, watch, computed, onMounted } from 'vue'
import CodeEditor from '@/components/codeeditor/CodeEditor'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    CodeEditor,
  },
  setup(props){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    
    const { templateId, stepPosition } = route.params

    const loading = ref(true)
    const stepTypeName = ref(null)
    const stepTypeParamMetas = ref({})
    const stepTypeOutputs = ref(null)

    let step = inject('step')

    onMounted(() => {
      pipelineApi.getStepType({ type: step.value.type }).then(resp => {
        const { name, params, outputs, type } = resp.data
        stepTypeName.value = name
        stepTypeOutputs.value = outputs

        params.sort((a,b) => a.displayOrder - b.displayOrder).forEach(p => {
          const paramName = p.name
          stepTypeParamMetas.value[paramName] = p
          
          if(!(paramName in step.value.params)){
            step.value.params[paramName] = null
          }
        })

        loading.value = false
      })
    })

    return {
      loading,

      stepTypeName,
      stepTypeOutputs,
      stepTypeParamMetas,
      step,
      stepPosition,

      onClickBack(){
        router.push(`/workspace/template/${templateId}/step`)
      },

    }
  }
};
</script>