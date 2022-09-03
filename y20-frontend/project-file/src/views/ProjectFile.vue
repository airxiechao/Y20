<template>
  <div class="project-file">
    <router-view />
  </div>
</template>

<style lang="scss">
.project-file{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { inject, computed, provide } from 'vue'

export default {
  components: {
  },
  setup(){
    const store = inject('useStore')()

    const projectName = computed(() => store.state.project.name || '')
    provide('projectName', projectName)
    
    store.commit('setTitle', { title: projectName.value })
    return {

    }
  },
};
</script>