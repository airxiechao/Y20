<template>
  <template v-if="loading">
    <q-skeleton type="text" animation="fade" />
    <q-skeleton type="text" animation="fade" width="50%" />
  </template>
  <div v-else class="markdown-viewer markdown" v-html="result">
  </div>
</template>

<style lang="scss">
@import '@/assets/scss/markdown';
</style>

<script>
import { ref, watch } from 'vue' 
import MarkdownIt from 'markdown-it'
const md = new MarkdownIt()

export default {
  props: ['source'],
  setup(props){
    const result = ref(null)
    const loading = ref(false)

    const render = (value) => {
      loading.value = true
      if(value){
        result.value = md.render(value)
        loading.value = false
      }
    }

    watch(() => props.source, (newValue) => {
      render(newValue)
    })

    render(props.source)

    return {
      result,
      loading,
    }
  },
}
</script>