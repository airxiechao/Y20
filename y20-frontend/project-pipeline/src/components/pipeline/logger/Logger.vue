<template>
  <div class="logger bg-black q-pa-sm flex">
    <div ref="el" class="relative-position full-width">
      <q-inner-loading :showing="loading" dark>
        <q-spinner-gears class="z-top" size="38px" color="grey-2" />
      </q-inner-loading>
    </div>
  </div>
</template>

<style lang="scss">
.logger{
  height: 416px;
  
  .xterm{
    width: 100%;
    height: 400px;

    .xterm-viewport{
      width: 100% !important;
      height: 100%;

      &::-webkit-scrollbar {
        background-color: #000;
        width: 8px;
      }

      &::-webkit-scrollbar-thumb {
        background: #777;
        border-radius: 4px;
      }
    }
  }

}

</style>

<script>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'

import 'xterm/css/xterm.css'
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { WebLinksAddon } from 'xterm-addon-web-links'

export default {
  props: {
    log: {
      type: String,
      required: true,
    },
    loading: {
      type: Boolean,
      required: false,
      default: false,
    },
    tail: {
      type: Boolean,
      required: false,
      default: false,
    },
  },
  setup(props){
    const el = ref(null)
    let term = null

    const scrollTo = () => {
      const headerOffset = 135
      const elementPosition = el.value.getBoundingClientRect().top
      const offsetPosition = elementPosition + window.pageYOffset - headerOffset
      window.scrollTo({
        top: offsetPosition,
        behavior: "smooth"
      })
    }

    watch(() => props.log , (newValue, oldValue) => {
      if(term){
        const log = newValue.substring(oldValue.length)
        term.write(log)
      }
    })

    const initTerm = () => {
      term = new Terminal({
        convertEol: true,
      })
      const fitAddon = new FitAddon()
      term.loadAddon(fitAddon)

      const webLinksAddon = new WebLinksAddon()
      term.loadAddon(webLinksAddon)

      term.open(el.value)
      term.write(props.log)

      fitAddon.fit()
      term.focus()

      const resizeObserver = new ResizeObserver(entries => {
        fitAddon.fit()
      })
      resizeObserver.observe(el.value)
    }

    onMounted(() => {
      initTerm()
      if(props.tail){
        scrollTo()
      }
    })

    onBeforeUnmount(() => {
      if(term){
        term.dispose()
      }
    })

    return {
      el,
    }
  }
}
</script>