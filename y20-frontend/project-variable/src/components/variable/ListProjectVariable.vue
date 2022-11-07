<template>
  <LayoutTwoColumn class="list-project-variable">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" :to="`/workspace/project`" />
            <q-toolbar-title></q-toolbar-title>
            <q-btn unelevated color="primary" label="创建变量" icon="add" @click="onClickCreateVariable" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-table
            flat
            class="list-project-variable-table"
            :rows="rows"
            :columns="columns"
            :loading="loading"
            :pagination="{ rowsPerPage: 10 }"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="name"
          >
            <template v-slot:top>
              <div class="page-heading">项目全局变量</div>
            </template>
            <template v-slot:body="props">
              <q-tr :props="props">
                <q-td class="cursor-pointer" @click="onClickVariable(props.row.name)">
                  <span>{{ props.row.name }}</span>
                </q-td>
                <q-td>
                  <q-badge v-if="props.row.kind == 'SECRET'" color="orange">机密</q-badge>
                  <span v-else>{{ props.row.value }}</span>
                </q-td>
                <q-td>
                  <q-btn color="negative" flat dense icon="delete" @click="onClickDeleteVairable(props.row.name)" />
                </q-td>
              </q-tr>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.list-project-variable{
  &-table{
    
  }
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
import { ref, inject, watch, computed, onMounted } from 'vue'

import projectApi from '@/api/project.api'

const columns = [
  {
    name: 'name',
    required: true,
    label: '变量名',
    align: 'left',
    field: row => row.name,
    format: val => `${val}`,
    sortable: true,
    headerStyle: "width: 50%;",
  },
  {
    name: 'value',
    required: false,
    label: '变量值',
    align: 'left',
    field: row => row.value || '',
    format: val => `${val}`,
    headerStyle: "width: 50%;",
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
    const { projectId } = route.params

    const projectVariables = ref({})
    const rows = computed(() => {
      return Object.values(projectVariables.value || {})
    })
    const loading = ref(false)

    const search = () => {
      loading.value = true
      projectApi.listVariable().then(resp => {
        loading.value = false
        projectVariables.value = resp.data
      }, resp => {
        loading.value = false
      })
    }
    
    onMounted(() => {
      search()
    })

    return {
      dayjs,
      projectId,
      projectName,

      rows,
      columns,
      loading,
      projectVariables,

      onClickCreateVariable(){
        router.push(`/project/${projectId}/variable/create`)
      },

      onClickVariable(name){
        router.push(`/project/${projectId}/variable/${name}`)
      },

      onClickDeleteVairable(variableName){
        $q.dialog({
          title: '删除',
          message: `确定删除变量 ${variableName} ？`,
          cancel: true,
        }).onOk(data => {
          projectApi.deleteVariable({ variableName }).then(resp => {
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