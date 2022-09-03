<template>
  <LayoutTwoColumn class="monitor-single">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="监视" :to="`/project/${projectId}/monitor`" />
          </q-toolbar>
        </div>

        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">
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
              
              <div>
                <q-btn v-if="isEdit" unelevated label="保存" type="submit" color="primary"/>
                <q-btn v-else unelevated label="创建" type="submit" color="primary"/>
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

import monitorApi from '@/api/monitor.api'

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

export default {
  components: {
    LayoutTwoColumn, SelectAgent
  },
  setup(){
    
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, monitorId } = route.params

    const isEdit = computed(() => !!monitorId)

    const monitor = ref({
      name: null,
      agentId: null,
      type: null,
      target: {},
    })

    watch(() => monitor.value.type, (newValue, oldValue)=>{
      if(oldValue){
        monitor.value.target = {}
      }
    })

    onMounted(() => {
      if(isEdit.value){
        monitorApi.get({ monitorId: monitorId }).then(resp => {
          monitor.value = resp.data
        })
      }
    })

    return {
      projectId,
      projectName,
      monitorId,
      monitor,

      monitorTypeOptions,
      isEdit,

      onSubmit(){
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