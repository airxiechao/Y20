<template>
  <div class="env-variable-list">
    <div class="tab-heading text-primary">环境变量</div>
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
          <q-td key="name" :props="props">
            {{ props.row.name }}
          </q-td>
          <q-td key="value" :props="props">
            {{ props.row.value }}
          </q-td>
        </q-tr>
      </template>
    </q-table>
    <div class="q-py-sm">
      <q-checkbox label="自动注入变量到环境变量" :model-value="step.params['injectVariableIntoEnv']" />
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
  },
]

export default {
  components: {
  },
  setup(){
    const step = inject('step')
    const rows = ref((()=>{
      const env = step.value.params['env'] || {}
      return Object.keys(env).map(k => {
        return {
          name: k,
          value: env[k],
        }})
    })())

    return {
      columns,
      rows,
      step,
    }
  }
};
</script>
