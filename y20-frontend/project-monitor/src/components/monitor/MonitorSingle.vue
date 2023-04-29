<template>
  <LayoutTwoColumn class="monitor-single">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="监视" :to="`/project/${projectId}/monitor`" />
          </q-toolbar>
        </div>

        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">
              <template v-if="isEdit">编辑监视</template>
              <template v-else>创建监视</template>
            </div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="monitor.name"
                :label="`名称 *`"
                hint="监视名称"
                lazy-rules
                :rules="[ 
                  val => val && val.length > 0 || '请输入监视名称',
                  val => val.length < 100 || '监视名称长度不超过100个字符'
                ]"
              />

              <SelectAgent
                bg-color="white"
                v-model="monitor.agentId"
                :label="`节点*`"
                hint="监视节点"
                lazy-rules
                :rules="[
                  (val) => (val && val.length > 0) || `请选择监视节点`,
                ]"
              />

              <q-select
                outlined
                v-model="monitor.type"
                :options="monitorTypeOptions" 
                :label="`监视类型 *`"
                hint="监视对象的类型"
                emit-value 
                map-options 
                lazy-rules
                :rules="[ 
                    val => val !== null || '请选择监视类型',
                ]"
              />

              <template v-if="monitor.type == 'PROCESS'">
                <q-input
                  outlined
                  v-model="monitor.target.commandPattern"
                  :label="`命令行匹配模式 *`"
                  hint="命令行匹配的正则表达式"
                  lazy-rules
                  :rules="[ 
                    val => val && val.length > 0 || '请输入命令行匹配模式',
                    val => val.length < 500 || '匹配模式长度不超过500个字符'
                  ]"
                />
              </template>
              <template v-if="monitor.type == 'SERVICE'">
                <q-input
                  outlined
                  v-model="monitor.target.namePattern"
                  :label="`服务名匹配模式 *`"
                  hint="服务名匹配的正则表达式"
                  lazy-rules
                  :rules="[ 
                    val => val && val.length > 0 || '请输入服务名匹配模式',
                    val => val.length < 100 || '服务名长度不超过100个字符'
                  ]"
                />
              </template>

              <q-select
                outlined
                v-model="monitor.actionType"
                :options="monitorActionTypeOptions" 
                :label="`动作类型 *`"
                hint="监视对象发生错误时的动作"
                emit-value 
                map-options 
                lazy-rules
                :rules="[ 
                    val => val !== null || '请选择动作类型',
                ]"
              />

              <template v-if="monitor.actionType == 'PIPELINE'">
                <div class="text-primary text-bold">动作参数</div>
                  <q-select
                    outlined
                    emit-value 
                    map-options 
                    bg-color="white"
                    :options="projects" 
                    label="项目 *" 
                    hint="选择项目"
                    v-model="monitor.actionParam.projectId"
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
                    v-model="monitor.actionParam.pipelineId"
                    @filter="onSelectPipeline"
                    :disable="!monitor.actionParam.projectId"
                    lazy-rules
                    :rules="[ val => !!val || '请选择流水线' ]"
                  />

                  <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" />
              </template>
              
              <div>
                <q-btn v-if="isEdit" unelevated label="保存" type="submit" color="primary"/>
                <q-btn v-else unelevated label="创建" type="submit" color="primary"/>
                <q-btn flat class="q-ml-sm bg-grey-2" label="取消" @click="onClickBack" /> 
              </div>
            </q-form>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.monitor-single{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'
import SelectAgent from '@/components/monitor/common/SelectAgent'
import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'

import monitorApi from '@/api/monitor.api'
import projectApi from '@/api/project.api'
import pipelineApi from '@/api/pipeline.api'

const monitorTypeOptions = [
  {
    label: '进程',
    value: 'PROCESS',
  },
  {
    label: '服务',
    value: 'SERVICE',
  },
]

const monitorActionTypeOptions = [
  {
    label: '无',
    value: 'NONE',
  },
  {
    label: '流水线',
    value: 'PIPELINE',
  },
]

export default {
  components: {
    LayoutTwoColumn, SelectAgent, PipelineInVariablesForm
  },
  setup(){
    
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')

    const { projectId, monitorId } = route.params
    const projects = ref([])
    const pipelines = ref([])
    const pipelineInVariables = ref([])

    const isEdit = computed(() => !!monitorId)

    const monitor = ref({
      name: null,
      agentId: null,
      type: null,
      target: {},
      actionType: null,
      actionParam: {
        projectId: null,
        pipelineId: null,
        inParams: {},
      },
    })

    const inParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })

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
      const projectId = monitor.value.actionParam.projectId
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
      const projectId = monitor.value.actionParam.projectId
      const pipelineId = monitor.value.actionParam.pipelineId
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
        
        const inParams = monitor.value.actionParam.inParams
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

    watch(() => monitor.value.type, (newValue, oldValue)=>{
      if(oldValue){
        monitor.value.target = {}
      }
    })

    watch(() => monitor.value.actionType, (newValue)=>{
      if(newValue == 'NONE'){
        monitor.value.actionParam.projectId = null
      }
    })

    watch(() => monitor.value.actionParam.projectId, (newValue, oldValue) => {
      if(oldValue){
        monitor.value.actionParam.pipelineId = null
      }
    })

    watch(() => monitor.value.actionParam.pipelineId, (newValue) => {
      if(!newValue){
        pipelineInVariables.value = []
        return
      }

      searchVariable()
    })

    onMounted(() => {
      if(isEdit.value){
        monitorApi.get({ monitorId: monitorId }).then(resp => {
          let data = resp.data
          if(!data.actionType){
            data.actionType = 'NONE'
          }

          if(!data.actionParam){
            data.actionParam = {
              projectId: null,
              pipelineId: null,
              inParams: {},
            }
          }

          monitor.value = data

          searchProject()
          searchPipeline()
          searchVariable()
        })
      }
    })

    return {
      projectId,
      projectName,
      monitorId,
      monitor,
      projects,
      pipelines,
      pipelineInVariables,

      monitorTypeOptions,
      monitorActionTypeOptions,
      isEdit,

      onClickBack(){
        router.back()
      },

      onSelectProject(inputValue, doneFn, abortFn){
        searchProject(inputValue, doneFn, abortFn)
      },

      onSelectPipeline(inputValue, doneFn, abortFn){
        searchPipeline(inputValue, doneFn, abortFn)
      },

      onSubmit(){
        monitor.value.actionParam.inParams = inParams.value
        
        if(isEdit.value){
          monitorApi.updateBasic({
            monitorId,
            ...monitor.value,
          }).then(resp => {
            router.push(`/project/${projectId}/monitor`)
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }else{
          monitorApi.create(monitor.value).then(resp => {
            router.push(`/project/${projectId}/monitor`)
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }
      },
    }
  }
};
</script>