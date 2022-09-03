<template>
  <div class="project-pipeline">
    <router-view />
  </div>
</template>

<style lang="scss">
.project-pipeline{
  
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

    return {

    }
  },
};
</script>