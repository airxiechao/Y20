<template>
  <div class="user-billing">
    <router-view />
  </div>
</template>

<style lang="scss">
.user-billing{
  
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
      store.commit('setTitle', { title: '配额' })
    })
  }
}
</script>