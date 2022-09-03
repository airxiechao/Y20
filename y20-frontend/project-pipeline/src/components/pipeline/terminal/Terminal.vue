<template>
  <div class="term bg-black q-pa-sm flex">
    <div class="relative-position full-width" ref="el"></div>
  </div>
</template>

<style lang="scss">
.term{
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
      }
    }
  }
}
</style>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'

import 'xterm/css/xterm.css'
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { AttachAddon } from 'xterm-addon-attach'

export default {
  props: ['socketUrl'],
  setup(props){
    const el = ref(null)
    let socket = null
    let term = null

    const initTerm = () => {
      term = new Terminal({
        convertEol: true,
        cursorBlink: true,
      })
      const attachAddon = new AttachAddon(socket)
      const fitAddon = new FitAddon()
      term.loadAddon(attachAddon)
      term.loadAddon(fitAddon)
      term.open(el.value)
      fitAddon.fit()
      term.focus()

      const resizeObserver = new ResizeObserver(entries => {
        fitAddon.fit()
      })
      resizeObserver.observe(el.value)
    }

    const initSocket = () => {
      socket = new WebSocket(props.socketUrl)
      socket.onopen = () => {
        console.log('terminal ws open')
        initTerm()
        //socket.send('\r')
      }
      socket.onclose = () => {
        console.log('terminal ws close')
      }
      socket.onerror = () => {
        console.log('terminal ws error')
      }
    }

    onMounted(() => {
      initSocket()
    })

    onBeforeUnmount(() => {
      if(socket){
        socket.close()
      }
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