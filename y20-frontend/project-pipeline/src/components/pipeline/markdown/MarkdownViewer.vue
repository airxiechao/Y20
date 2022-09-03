<template>
  <div class="markdown-viewer markdown q-mt-sm q-pa-sm" v-html="result">
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

    const render = (value) => {
      result.value = md.render(value || '');
    }

    watch(() => props.source, (newValue) => {
      render(newValue)
    })

    render(props.source);

    return {
      result,
    }
  },
}
</script>