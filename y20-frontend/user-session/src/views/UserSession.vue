<template>
  <div class="user-session">
    <router-view />
  </div>
</template>

<style lang="scss">
.user-session{
  
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
      store.commit('setTitle', { title: '会话' })
    })
  }
}
</script>