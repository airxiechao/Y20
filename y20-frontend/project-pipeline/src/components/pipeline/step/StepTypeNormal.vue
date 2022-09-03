<template>
  <LayoutTwoColumn class="step-type-normal">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded no-caps flat color="primary" icon="keyboard_backspace" 
              :label="isModeEdit?'步骤列表':'步骤类型列表'"  @click="onClickBack" />
          </q-toolbar>
        </div>

        <div class="q-pa-md page-content" style="position:relative;">
          <q-card class="q-pa-md">
            <div class="q-pb-md">
              <div>
                步骤类型 <span v-if="stepTypeName">- {{ stepTypeName }}</span>
              </div>
              <q-skeleton v-if="loading" type="text" animation="fade" style="max-width: 150px;" />
              <q-breadcrumbs v-else class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el :label="`${pipelineName}`"/>
                <q-breadcrumbs-el :label="`第 ${parseInt(stepPosition)+1} 步`" />
              </q-breadcrumbs>
            </div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input
                outlined 
                bg-color="white" 
                label="名称 *"
                v-model="step.name" 
                lazy-rules
                :rules="[
                  (val) => (val && val.length > 0) || '请输入名称',
                  (val) => val.length <= 100 || '名称长度不超过100个字符',
                ]" 
              />

              <template v-if="stepTypeRequires && stepTypeRequires.length > 0">
                <div class="text-primary text-bold">依赖</div>
                <ul class="q-mb-lg">
                  <li v-for="(require, i) in stepTypeRequires" :key="i">
                    {{ require }}
                  </li>
                </ul>
              </template>
            
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
                    outlined
                    bg-color="white"
                    :label="`${meta.displayName} ${metaRequired(name)?'*':''}`"
                    :hint="`${meta.hint}`"
                    v-model="step.params[name]" 
                    :rules="[ val => (metaRequired(name) ? !!val : true) || meta.hint ]"
                  />
                  <q-field v-else-if="meta.type == 'TEXT'" 
                    borderless
                    stack-label
                    bg-color="white"
                    :label="`${meta.displayName} ${metaRequired(name)?'*':''}`" 
                    :hint="`${meta.hint}`"
                    v-model="step.params[name]" 
                    :rules="[ val => (metaRequired(name) ? !!val : true) || meta.hint ]"
                  >
                    <CodeEditor v-model="step.params[name]"/>
                  </q-field>
                  <!-- <q-input v-else-if="meta.type == 'TEXT'" 
                    outlined
                    type="textarea" 
                    bg-color="white"
                    :label="`${meta.displayName} ${metaRequired(name)?'*':''}`" 
                    :hint="`${meta.hint}`"
                    v-model="step.params[name]" 
                    :rules="[ val => (metaRequired(name) ? !!val : true) || meta.hint ]" /> -->
                  <q-select v-else-if="meta.type == 'SELECT'"
                    outlined
                    emit-value 
                    map-options 
                    bg-color="white"
                    :options="meta.options" 
                    :label="`${meta.displayName} ${metaRequired(name)?'*':''}`" 
                    :hint="`${meta.hint}`"
                    v-model="step.params[name]" 
                    :rules="[ val => (metaRequired(name) ? !!val : true) || meta.hint ]"
                  />
                  <div v-else-if="meta.type == 'FILE'">
                    <q-input
                      outlined
                      bg-color="white"
                      v-model="step.params[name]" 
                      :hint="`${meta.hint}`"
                      :label="`${meta.displayName} ${metaRequired(name)?'*':''}`" 
                      :rules="[ val => (metaRequired(name) ? !!val : true) || meta.hint ]"
                    />
                    <q-uploader
                      class="q-mt-md q-ml-md"
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
              <q-list v-else dense separator class="rounded-borders bg-grey-2 text-grey-7">
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
                <q-btn unelevated type="submit" class="q-mr-sm" color="primary" :label="isModeEdit ? '保存' : '添加'" />
                <q-btn flat label="返回" @click="onClickBack" />
              </div>
            </q-form>
          
          </q-card>

          <!-- <q-inner-loading :showing="loading">
            <q-spinner-gears size="30px" color="primary" />
          </q-inner-loading> -->
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
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

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import PipelineStepFileUpload from '@/components/pipeline/file/mixin/PipelineStepFileUpload.mixin'
import CodeEditor from '@/components/pipeline/codeeditor/CodeEditor'

import pipelineApi from '@/api/pipeline.api'

export default {
  mixins: [ PipelineStepFileUpload ],
  components: {
    LayoutTwoColumn,
    CodeEditor,
  },
  setup(props){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, stepPosition } = route.params
    const isModeEdit = computed(() => !route.params.stepType)
    const loading = ref(true)
    const stepTypeName = ref(null)
    const stepType = ref(null)
    const stepTypeParamMetas = ref({})
    const stepTypeOutputs = ref(null)
    const stepTypeRequires = ref(null)
    let step = inject('step')
    if(!step.value){
      step = ref({
        name: null,
        params: {},
      })
      stepType.value = route.params.stepType
    }else{
      stepType.value = step.value.type
    }

    onMounted(() => {
      pipelineApi.getStepType({ type: stepType.value }).then(resp => {
        const { name, params, outputs, requires, type } = resp.data
        stepTypeName.value = name
        stepTypeOutputs.value = outputs
        stepTypeRequires.value = requires

        if(!step.value.name){
          step.value.name = `${name} ` + dayjs().format(`YYYYMMDDHHmmss`)
        }

        params.sort((a,b) => a.displayOrder - b.displayOrder).forEach(p => {
          const paramName = p.name
          stepTypeParamMetas.value[paramName] = p
          
          if(!(paramName in step.value.params)){
            step.value.params[paramName] = p.defaultValue || null
          }
        })

        
        loading.value = false
      })
    })

    const onClickAddStep = () => {
      pipelineApi.addStep({ 
        pipelineId,
        type: stepType.value,
        name: step.value.name,
        params: step.value.params,
        position: stepPosition,
      }).then(resp => {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
      })
    }

    const onClickSaveStep = () => {
      pipelineApi.updateStep({ 
        pipelineId,
        position: stepPosition,
        step: step.value,
      }).then(resp => {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
      })
    }

    return {
      loading,
      isModeEdit,
      pipelineName,
      stepPosition,
      stepTypeName,
      stepTypeOutputs,
      stepTypeRequires,
      stepTypeParamMetas,
      step,

      metaRequired(name){
        const meta = stepTypeParamMetas.value[name]
        if(!meta){
          return false
        }

        return meta.required && (meta.conditionalOnRequiredTrue?step.value.params[meta.conditionalOnRequiredTrue]=='true':true)
      },

      onUploaded({files, xhr}, name){
        let resp = JSON.parse(xhr.response)
        step.value.params[name] = resp.data
      },

      onClickBack(){
        if(isModeEdit.value){
          router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
        }else{
          router.push(`/project/${projectId}/pipeline/${pipelineId}/step/${stepPosition}/add`)
        }
      },

      onSubmit(){
        if(isModeEdit.value){
          onClickSaveStep()
        }else{
          onClickAddStep()
        }
      },

    }
  }
};
</script>