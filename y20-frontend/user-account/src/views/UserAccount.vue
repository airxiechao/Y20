<template>
  <div class="user-account">
    <router-view />
  </div>
</template>

<style lang="scss">
.user-account{
  
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
      store.commit('setTitle', { title: '账号' })
    })
  }
}
</script>