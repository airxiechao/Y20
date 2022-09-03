<template>
  <div class="env-variable-list">
    <q-dialog v-model="flagShowDialog" persistent>
      <q-card style="min-width: 350px">
        <q-card-section>
          <div class="text-h6">{{variableFormMode}}环境变量</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form
            ref="variableForm"
            @submit="onSubmitVariable"
            class="q-gutter-md"
          >
            <q-input 
              outlined
              :disable="variableFormMode == '编辑'"
              label="变量名"
              hint="请输入变量名"
              v-model="variableName" 
              lazy-rules
              :rules="[ 
                (val) => (val && val.length > 0) || '请输入变量名',
                (val) => val.length <= 50 || '变量名长度不超过50个字符',
                (val) => (variableFormMode == '编辑' ? true : rows.filter(v => v.name === val).length == 0) || '变量名已存在'
              ]"
            />
            <q-input 
              outlined
              label="变量值"
              hint="请输入变量值"
              v-model="variableValue"
              lazy-rules
              :rules="[ 
                val => !!val || '请输入变量值',
              ]"
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat color="primary" label="取消" v-close-popup/>
          <q-btn flat color="primary" label="确定" @click="variableForm.submit()" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <div class="row">
      <div class="col tab-heading text-primary">环境变量</div>
      <q-btn unelevated color="primary" label="创建环境变量" @click="onClickShowDialog" />
    </div>
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="name"
      :rows-per-page-options="[5, 10, 20, 50]"
      :pagination="{
        sortBy: 'name',
        rowsPerPage: 10,
      }"
    >
      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td key="name" :props="props" class="cursor-pointer" @click="onClickVariable(props.row.name, props.row.value)">
            {{ props.row.name }}
          </q-td>
          <q-td key="value" :props="props" class="cursor-pointer" @click="onClickVariable(props.row.name, props.row.value)">
            {{ props.row.value }}
          </q-td>
          <q-td key="action" :props="props">
            <q-btn color="negative" dense flat icon="delete" @click="onClickDeleteVariable(props.row.name)" />
          </q-td>
        </q-tr>
      </template>
    </q-table>
    <div class="q-py-sm">
      <q-checkbox label="自动注入变量到环境变量" v-model="step.params['injectVariableIntoEnv']" />
    </div>

  </div>
</template>

<style lang="scss">
.env-variable-list{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { ref, inject, computed, watch } from 'vue'

const columns = [
  {
    name: 'name',
    required: true,
    label: '变量名',
    align: 'left',
    field: row => row.name,
    format: val => `${val}`,
    sortable: true,
    headerStyle: "width: 45%;",
  },{
    name: 'value',
    required: false,
    label: '变量值',
    align: 'left',
    field: row => row.value || '',
    format: val => `${val}`,
    headerStyle: "width: 45%;",
  },{
    name: 'action',
    label: '',
    align: 'right',
  }
]

export default {
  components: {
  },
  setup(){
    const $q = inject('useQuasar')()
    const step = inject('step')
    const rows = ref((()=>{
      const env = step.value.params['env'] || {}
      return Object.keys(env).map(k => {
        return {
          name: k,
          value: env[k],
        }})
    })())

    const newEnv = computed(() => {
      const ret = {}
      rows.value.forEach(v => {
        ret[v['name']] = v['value']
      })
      return ret
    })

    const flagShowDialog = ref(false)
    const variableFormMode = ref('')
    const variableForm = ref(null)
    const variableName = ref('')
    const variableValue = ref('')

    watch(newEnv, (newValue, oldValue) => {
      step.value.params['env'] = newValue
    })

    const clearPromptAdd = () => {
      variableName.value = ''
      variableValue.value = ''
    }

    return {
      columns,
      rows,

      step,

      flagShowDialog,
      variableFormMode,
      variableForm,
      variableName,
      variableValue,

      onClickShowDialog(){
        clearPromptAdd()
        variableFormMode.value = '创建'
        flagShowDialog.value = true
      },

      onClickVariable(name, value){
        variableFormMode.value = '编辑'
        variableName.value = name
        variableValue.value = value
        flagShowDialog.value = true
      },

      onSubmitVariable(){
        const exists = rows.value.filter(v => v.name === variableName.value).length > 0
        if(exists){
          for(let i = 0; i < rows.value.length; ++i){
            const v = rows.value[i]
            if(v.name === variableName.value){
              v.value = variableValue.value
            }
          }
        }else{
          rows.value.push({
            name: variableName.value,
            value: variableValue.value,
          })
        }
        
        flagShowDialog.value = false
      },

      onClickDeleteVariable(name){
        $q.dialog({
          title: '删除',
          message: `确定删除环境变量 ${name} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          rows.value = rows.value.filter(v => v.name !== name)
        })
      },

    }
  }
};
</script>
