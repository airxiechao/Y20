<template>
  <q-select
    outlined 
    use-input
    new-value-mode="add"
    :options="agents" 
    label="节点"
    hint="选择节点。输入 {{变量名}} 回车，可以引用变量"
    emit-value 
    map-options 
    @filter="onSelectAgent"
  >
    <template v-slot:prepend>
      <q-icon name="computer" />
    </template>
    <template v-slot:no-option>
      <div class="q-pa-md">
        <span class="vertical-middle text-grey">没有节点？</span>
        <q-btn class="q-mx-xs vertical-middle" color="primary" dense flat label="接入一个节点" to="/workspace/agent/join" />
      </div>
    </template>
  </q-select>
</template>

<script>
import { ref, inject } from 'vue'
import agentApi from '@/api/agent.api'

export default {
  setup(){
    const $q = inject('useQuasar')()
    const router = inject('useRouter')()
    const agents = ref([])

    const searchAgent = (inputValue, doneFn, abortFn) => {
      agentApi.listAgent({ 
        agentId: inputValue
      }).then(resp => {
        agents.value =  resp.data.page.map(a => {
          return {
            label: a.agentId + ' - ' + a.os + ' - ' + (a.active ? '在线' : '离线'),
            value: a.agentId,
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

    return {
      agents,

      onSelectAgent(inputValue, doneFn, abortFn){
        searchAgent(inputValue, doneFn, abortFn)
      },

    }
  },
}
</script>