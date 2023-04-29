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
  },
  setup(props, { emit }){
    const editor = ref(null)
    let cm = null

    const initCodeMirror = () => {
      if(null == cm){
        cm = CodeMirror.fromTextArea(editor.value, {
          value: props.modelValue || '',
          lineNumbers: true,
          styleActiveLine: true,
          theme: 'darcula',
          mode: 'shell',
        })

        cm.on("change", () => {
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
        let pos = cm.getCursor();
        cm.setValue(value || '');
        cm.setCursor(pos);
      }
    }

    const insertCodeMirror = (value) => {
      if(cm){
        if(cm.somethingSelected()) {
          cm.replaceSelection(value);
          return;
        }

        let pos = cm.getCursor();
        cm.replaceRange(value, pos);
        cm.focus();
      }
    }

    watch(() => props.modelValue, (newValue, oldValue) => {
      updateCodeMirror(newValue)
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
      },

      insert(value){
        insertCodeMirror(value)
      }
    }
  }
}
</script>