<template>
  <div class="workspace-template">
    <router-view />
  </div>
</template>

<style lang="scss">
.workspace-template{
  
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
      
    })
  }
}
</script>