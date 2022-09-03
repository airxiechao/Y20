<template>
  <div class="project-variable">
    <router-view />
  </div>
</template>

<style lang="scss">
.project-variable{
  
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { inject, computed, provide, onMounted } from 'vue'

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