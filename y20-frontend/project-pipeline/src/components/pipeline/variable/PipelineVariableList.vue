<template>
  <div class="pipeline-variable-list">
    <div class="row q-pb-md">
      <div class="col page-heading text-primary self-center">流水线变量</div>
      <div class="self-center">
        <q-btn flat class="bg-blue-1" icon="add" color="primary" label="创建变量" @click="onClickCreateVariable" />
      </div>
    </div>
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      :loading="loading"
      row-key="name"
      :pagination="{
        sortBy: 'order',
        descending: false,
        rowsPerPage: 20,
      }"
      :rows-per-page-options="[5, 10, 20, 50]"
    >
      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td>
            <span>{{ props.row.order }}</span>
          </q-td>
          <q-td class="cursor-pointer" @click="onClickVariable(props.row.name)">
            <span>{{ props.row.name }}</span>
          </q-td>
          <q-td>
            <q-badge v-if="props.row.kind == 'IN'" color="green">输入</q-badge>
            <q-badge v-else-if="props.row.kind == 'SECRET'" color="orange">机密</q-badge>
            <q-badge v-else-if="props.row.kind == 'PROJECT_VARIABLE_REFERENCE'" color="blue">引用项目变量 {{ props.row.value }}</q-badge>
            <span v-else>{{ props.row.value }}</span>
          </q-td>
          <q-td>
            <q-btn color="negative" flat dense icon="delete" @click="onClickDeleteVairable(props.row.name)" />
          </q-td>
        </q-tr>
      </template>
    </q-table>
  </div>
</template>

<style lang="scss">
.pipeline-variable-list{
  
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
import { ref, inject, computed, onMounted } from 'vue'

import pipelineApi from '@/api/pipeline.api'

const columns = [
  {
    name: 'order',
    label: '序号',
    align: 'left',
    field: row => row.order,
    format: val => `${val}`,
    sortable: true,
    headerStyle: "width: 5%;",
  },
  {
    name: 'name',
    required: true,
    label: '变量名',
    align: 'left',
    field: row => row.name,
    format: val => `${val}`,
    sortable: true,
    headerStyle: "width: 45%;",
  },
  {
    name: 'value',
    required: false,
    label: '变量值',
    align: 'left',
    field: row => row.value || '',
    format: val => `${val}`,
    headerStyle: "width: 45%;",
  },
  {
    name: 'action',
  }
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, pipelineId } = route.params

    const pipelineVariables = ref({})
    const rows = computed(() => {
      return Object.values(pipelineVariables.value || {})
    })
    const loading = ref(false)

    const search = () => {
      loading.value = true
      pipelineApi.listVariable({ pipelineId }).then(resp => {
        loading.value = false
        const data = resp.data
        let newVariables = {}
        if(data){
          const values =  Object.values(data)
          values.sort((a,b) => (a.order || 0) - (b.order || 0))
          values.forEach(v => {
            newVariables[v.name] = v
          })
        }

        pipelineVariables.value = newVariables
      }, resp => {
        loading.value = false
      })
    }
    
    onMounted(() => {
      search()
    })

    return {
      projectName,

      rows,
      columns,
      loading,
      pipelineVariables,

      onClickCreateVariable(){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/variable/create`)
      },

      onClickVariable(name){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/variable/${name}`)
      },

      onClickDeleteVairable(variableName){
        $q.dialog({
          title: '删除',
          message: `确定删除变量 ${variableName} ？`,
          cancel: true,
        }).onOk(data => {
          pipelineApi.deleteVariable({ pipelineId, variableName }).then(resp => {
            search()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      }

    }
  }
};
</script>