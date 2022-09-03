<template>
  <div class="workspace-agent">
    <router-view />
  </div>
</template>

<style lang="scss">
.workspace-agent{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { inject, onMounted } from 'vue'

export default {
  setup(){
    const store = inject('useStore')()

    onMounted(() => {
      store.commit('setTitle', { title: '节点' })
    })
  }
}
</script>