<template>
  <LayoutTwoColumn class="step-type-call-pipeline">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded no-caps flat color="primary" icon="keyboard_backspace" 
              :label="isModeEdit?'步骤列表':'步骤类型列表'"  @click="onClickBack" />
          </q-toolbar>
        </div>

        <div class="q-pa-sm page-content" style="position:relative;">
          <q-card flat class="q-pa-md">
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
              <q-input outlined bg-color="white" label="名称 *" v-model="step.name" 
                lazy-rules
                :rules="[
                  (val) => (val && val.length > 0) || '请输入名称',
                  (val) => val.length <= 100 || '名称长度不超过100个字符',
                ]" 
              />
            
              <div class="text-primary text-bold">参数</div>
              <q-card v-if="loading" flat class="q-pa-sm">
                <div class="row q-mb-sm" v-for="pi in [1,2,3]" :key="pi">
                  <div class="q-pr-sm" style="width: 80px;"><q-skeleton type="text" animation="fade" /></div>
                  <div class="col"><q-skeleton type="text" animation="fade" /></div>
                </div>
              </q-card>
              <template v-else>
                <q-select
                  outlined
                  emit-value 
                  map-options 
                  bg-color="white"
                  :options="projects" 
                  label="项目 *" 
                  hint="选择项目"
                  v-model="step.params['projectId']"
                  @filter="onSelectProject"
                  lazy-rules
                  :rules="[ val => !!val || '请选择项目' ]"
                />

                <q-select
                  outlined
                  emit-value 
                  map-options 
                  bg-color="white"
                  :options="pipelines" 
                  label="流水线 *" 
                  hint="选择流水线"
                  v-model="step.params['pipelineId']"
                  @filter="onSelectPipeline"
                  :disable="!step.params['projectId']"
                  lazy-rules
                  :rules="[ val => !!val || '请选择流水线' ]"
                />

                <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" />

                <q-input
                  outlined
                  bg-color="white"
                  label="输出变量前缀 *"
                  hint="被调用流水线的输出变量将以添加前缀的方式作为步骤输出"
                  v-model="step.params['outParamPrefix']" 
                  :rules="[ 
                    val => val && val.length > 0 || '请输入输出变量前缀',
                    val => val.length < 50 || '输出变量前缀长度不超过50个字符'
                  ]"
                />

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

import { LayoutTwoColumn } from 'common'
import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'
import { ref, inject, computed, watch, onMounted } from 'vue'

import projectApi from '@/api/project.api'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    LayoutTwoColumn,
    PipelineInVariablesForm,
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
    const projects = ref([])
    const pipelines = ref([])
    const pipelineInVariables = ref([])

    const inParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })

    const randomName = (length = 3) => {
      let chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
      let str = '';
      for (let i = 0; i < length; i++) {
          str += chars.charAt(Math.floor(Math.random() * chars.length))
      }

      return str
    }

    let step = inject('step')
    if(!step.value){
      step = ref({
        name: null,
        params: {
          projectId: null,
          pipelineId: null,
          inParams: {},
          outParamPrefix: randomName(3)+'_',
        },
      })
      stepType.value = route.params.stepType
    }else{
      stepType.value = step.value.type
    }

    const searchProject = (inputValue, doneFn, abortFn) => {
      projectApi.list({ 
        name: inputValue
       }).then(resp => {
        projects.value = resp.data.page.map(a => {
          return {
            label: a.name,
            value: a.projectId,
          }
        })

        if(doneFn){
          doneFn()
        }
      }, resp => {
        if(abortFn){
          abortFn()
        }
      })
    }

    const searchPipeline = (inputValue, doneFn, abortFn) => {
      const projectId = step.value.params['projectId']
      if(!projectId){
        return
      }
      
      pipelineApi.list({ 
        projectId,
        name: inputValue,
       }).then(resp => {
        pipelines.value = resp.data.page.map(a => {
          return {
            label: a.name,
            value: a.id,
          }
        })

        if(doneFn){
          doneFn()
        }
      }, resp => {
        if(abortFn){
          abortFn()
        }
      })
    }

    const searchVariable = () => {
      const projectId = step.value.params['projectId']
      const pipelineId = step.value.params['pipelineId']
      if(!projectId || !pipelineId){
        return
      }

      pipelineApi.listVariable({ 
        projectId,
        pipelineId,
      }).then(resp => {
        pipelineInVariables.value = Object.values(resp.data).sort((a,b) => (a.order || 0) - (b.order || 0)).filter(v => v.kind == 'IN')
        pipelineInVariables.value.forEach(v => {
          v.value = v.defaultValue || null
        })
        
        const inParams = step.value.params['inParams']
        if(inParams){
          Object.keys(inParams).forEach(k => {
            const i = pipelineInVariables.value.findIndex(v => v.name === k)
            if(i >= 0){
              pipelineInVariables.value[i].value = inParams[k]
            }
          })
        }
      })
    }

    watch(() => step.value.params['projectId'], () => {
      step.value.params['pipelineId'] = null
    })

    watch(() => step.value.params['pipelineId'], (newValue) => {
      if(!newValue){
        pipelineInVariables.value = []
        return
      }

      searchVariable()

    })

    onMounted(() => {
      searchProject()
      searchPipeline()
      searchVariable()

      pipelineApi.getStepType({ type: stepType.value }).then(resp => {
        const { name, params, outputs, type } = resp.data
        stepTypeName.value = name
        stepTypeOutputs.value = outputs

        if(!step.value.name){
          step.value.name = dayjs().format(`${name} YYYYMMDDHHmmss`)
        }

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

    const onClickAddStep = () => {
      step.value.params['inParams'] = inParams.value

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
      step.value.params['inParams'] = inParams.value

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
      stepTypeParamMetas,
      step,

      projects,
      pipelines,
      pipelineInVariables,
      inParams,

      onSelectProject(inputValue, doneFn, abortFn){
        searchProject(inputValue, doneFn, abortFn)
      },

      onSelectPipeline(inputValue, doneFn, abortFn){
        searchPipeline(inputValue, doneFn, abortFn)
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