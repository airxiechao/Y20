<template>
  <div :class="{
    'code-editor': true, 
    'full-width': true, 
    'q-mt-sm': true, 
    'readonly': readonly, 
    }">
    <textarea ref="editor" :value="modelValue" @input="updateValue" />
  </div>
</template>

<style lang="scss">
.code-editor{
  border: 1px solid #ccc;
  overflow: hidden;

  &.readonly{
    border: 1px dashed #ccc;
  }

  .CodeMirror{
    font-family: Consolas, Menlo, Monaco, "Lucida Console", "Liberation Mono", "DejaVu Sans Mono", "Bitstream Vera Sans Mono", "Courier New", monospace;
  }
}
</style>

<script>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import CodeMirror from "codemirror"
import 'codemirror/addon/selection/active-line'
import 'codemirror/mode/shell/shell'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/darcula.css'

export default {
  props: {
    modelValue: '',
    readonly: false,
  },
  setup(props, { emit }){
    const editor = ref(null)
    let cm = null
    let changed = false

    const initCodeMirror = () => {
      if(null == cm){
        cm = CodeMirror.fromTextArea(editor.value, {
          value: props.modelValue || '',
          readOnly: props.readonly,
          lineNumbers: true,
          styleActiveLine: true,
          theme: 'darcula',
          mode: 'shell',
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