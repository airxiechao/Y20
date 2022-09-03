<template>
  <div>
    <MarkdownViewer :source="template.description" />
    <!-- <q-input
      v-model="template.description"
      autogrow
      readonly
      borderless 
    /> -->
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { ref, inject } from 'vue'
import MarkdownViewer from '@/components/markdown/MarkdownViewer'

export default {
  components: {
    MarkdownViewer,
  },
  setup(){
    const template = inject('template')

    return {
      dayjs,

      template,
    }
  },
}
</script>