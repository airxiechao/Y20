<template>
  <div class="user-token">
    <router-view />
  </div>
</template>

<style lang="scss">
.user-token{
  
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
      store.commit('setTitle', { title: '令牌' })
    })
  }
}
</script>