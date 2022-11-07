<template>
  <div class="step-type-normal" style="position:relative;">
    <div>
      <div class="row q-pb-md">
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
          <template v-for="(meta, name) in stepTypeParamMetas" :key="name">
            <q-input v-if="meta.type == 'INPUT'"
              readonly
              outlined
              :label="`${meta.displayName} ${meta.required?'*':''}`"
              :hint="`${meta.hint}`"
              v-model="step.params[name]" 
              :rules="[ val => (meta.required ? !!val : true) || meta.hint ]"
            />
            <q-field v-else-if="meta.type == 'TEXT'" 
              borderless
              readonly
              stack-label
              bg-color="white"
              :label="`${meta.displayName} ${meta.required?'*':''}`" 
              :hint="`${meta.hint}`"
              v-model="step.params[name]" 
              :rules="[ val => (meta.required ? !!val : true) || meta.hint ]"
            >
              <CodeEditor v-model="step.params[name]" :readonly="true" />
            </q-field>
            <!-- <q-input v-else-if="meta.type == 'TEXT'" 
              readonly
              type="textarea" 
              outlined
              :label="`${meta.displayName} ${meta.required?'*':''}`" 
              :hint="`${meta.hint}`"
              v-model="step.params[name]" 
              :rules="[ val => (meta.required ? !!val : true) || meta.hint ]" /> -->
            <q-select v-else-if="meta.type == 'SELECT'" 
              readonly
              outlined
              emit-value 
              map-options 
              :options="meta.options" 
              :label="`${meta.displayName} ${meta.required?'*':''}`" 
              :hint="`${meta.hint}`"
              v-model="step.params[name]" 
              :rules="[ val => (meta.required ? !!val : true) || meta.hint ]"
            />
            <div v-else-if="meta.type == 'FILE'">
              <q-input
                readonly
                outlined
                v-model="step.params[name]" 
                :hint="`${meta.hint}`"
                :label="`${meta.displayName} ${meta.required?'*':''}`" 
                :rules="[ val => (meta.required ? !!val : true) || meta.hint ]"
              />
              <q-uploader class="q-mt-md q-ml-md"
                readonly
                label="上传文件"
                :url="uploadPipelineStepFileUrl"
                :headers="uploadPipelineStepFileHeaders"
                :formFields="uploadPipelineStepFileFields"
                fieldName="file"
                auto-upload
                @uploaded="(info) => onUploaded(info, name)"
              />
            </div>
          </template>
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
          <q-btn unelevated color="primary" label="返回" @click="onClickBack" />
        </div>
      </q-form>
    </div>
    <!-- <q-inner-loading :showing="loading">
      <q-spinner-gears size="30px" color="primary" />
    </q-inner-loading> -->
  </div>
</template>

<style lang="scss">
.step-type-normal{
  
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