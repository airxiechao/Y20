<template>
  <div class="code-editor full-width q-mt-sm">
    <textarea ref="editor" :value="modelValue" @input="updateValue" />
  </div>
</template>

<style lang="scss">
.code-editor{
  border: 1px solid #ccc;
  overflow: hidden;

  .CodeMirror{
    font-family: inherit;
  }
}
</style>

<script>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import CodeMirror from "codemirror"
import 'codemirror/lib/codemirror.css'

export default {
  props: {
    modelValue: '',
  },
  setup(props, { emit }){
    const editor = ref(null)
    let cm = null
    let changed = false

    const initCodeMirror = () => {
      if(null == cm){
        cm = CodeMirror.fromTextArea(editor.value, {
          value: props.modelValue || '',
          lineNumbers: true,
        })

        cm.on("change", () => {
          changed = true
          emit("update:modelValue", cm.getValue())
        })
      }
    }

    const destroyCodeMirror = () => {
      if (null != cm) {
        cm.toTextArea()
        cm = null
      }
    }

    const updateCodeMirror = (value) => {
      if(cm){
        cm.setValue(value || '')
      }
    }

    watch(() => props.modelValue, (newValue, oldValue) => {
      if(!changed){
        updateCodeMirror(newValue)
      }
    })

    onMounted(() => {
      initCodeMirror()
    })

    onBeforeUnmount(() => {
      destroyCodeMirror()
    })

    return {
      editor,

      updateValue(event) {
        emit('update:modelValue', event.target.value);
      }
    }
  }
}
</script>