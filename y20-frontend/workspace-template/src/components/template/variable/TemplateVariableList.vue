<template>
  <div class="template-variable-list">
    <div class="page-heading text-primary q-pb-md">流水线变量</div>
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="name"
      :pagination="{
        sortBy: 'order',
        descending: false,
      }"
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
        </q-tr>
      </template>
    </q-table>
  </div>
</template>

<style lang="scss">
.template-variable-list{
  
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
    headerStyle: "width:45%;",
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
    const { templateId } = route.params

    const template = inject('template')

    const rows = computed(() => {
      return Object.values(template.value.variables || {})
    })

    return {
      rows,
      columns,

      onClickVariable(name){
        router.push(`/workspace/template/${templateId}/variable/${name}`)
      },
    }
  }
};
</script>