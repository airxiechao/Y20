<template>
  <div class="user-team">
    <router-view />
  </div>
</template>

<style lang="scss">
.user-team{
  
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
      store.commit('setTitle', { title: '团队' })
    })
  }
}
</script>