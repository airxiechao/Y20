<template>
  <LayoutTwoColumn class="single-project-variable">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="变量" :to="`/project/${projectId}/variable`" />
          </q-toolbar>
        </div>

        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">
              <template v-if="isEdit">编辑变量</template>
              <template v-else>创建变量</template>
            </div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-sm"
            >
              <q-input
                  :disable="isEdit"
                  outlined
                  v-model="variable.name"
                  :label="`变量名 *`"
                  lazy-rules
                  :rules="[ 
                    val => val && val.length > 0 || '请输入变量名',
                    val => val.length < 20 || '变量名长度不超过20个字符'
                  ]"
              />
              <div class="row">
                <div class="col-8 q-pr-sm">
                  <q-input v-if="variable.paramType == 'INPUT'"
                      outlined
                      v-model="variable.value"
                      :label="`变量值`"
                  />
                  <q-input v-else-if="variable.paramType == 'TEXT'"
                      type="textarea"
                      outlined
                      v-model="variable.value"
                      :label="`变量值`"
                  />
                </div>
                <div class="col">
                  <q-select filled emit-value map-options 
                      label="值类型"
                      v-model="variable.paramType" 
                      :options="paramTypeOptions" />
                </div>
              </div>
              
              <div class="q-gutter-sm">
                <q-radio class="q-ml-none" v-model="variable.kind" val="NORMAL" label="普通" />
                <q-radio v-model="variable.kind" val="SECRET" label="机密" />
             </div>
              
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
.single-project-variable{
  
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

import projectApi from '@/api/project.api'

const paramTypeOptions = [
  {
    label: '单行文本',
    value: 'INPUT',
  },
  {
    label: '多行文本',
    value: 'TEXT',
  },
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, projectVariableName } = route.params

    const isEdit = computed(() => !!projectVariableName)

    const variable = ref({
      name: null,
      value: null,
      paramType: 'INPUT',
      kind: 'NORMAL',
    })

    onMounted(() => {
      if(isEdit.value){
        projectApi.getVariable({ name: projectVariableName }).then(resp => {
          variable.value = resp.data
          secret.value = resp.data.kind == 'SECRET'
        })
      }
    })

    return {
      projectId,
      projectName,
      projectVariableName,
      variable,

      paramTypeOptions,
      isEdit,

      onClickBack(){
        router.back()
      },

      onSubmit(){
        if(isEdit.value){
          projectApi.updateVariable(variable.value).then(resp => {
            router.push(`/project/${projectId}/variable`)
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }else{
          projectApi.createVariable(variable.value).then(resp => {
            router.push(`/project/${projectId}/variable`)
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }
      },
    }
  }
};
</script>