<template>
  <q-layout style="min-height: unset !important;" class="layout-two-column" view="hHh lpR fFf">

    <q-drawer class="drawer" show-if-above v-model="rightDrawerOpen" side="right">
      <slot name="right"></slot>
    </q-drawer>

    <q-page-container :class="{
      'right-drawer-close': !rightDrawerOpen
    }">
      <slot name="center"></slot>
    </q-page-container>

  </q-layout>
</template>

<style lang="scss" scoped>
.layout-two-column{
  .drawer{
    padding-top: 50px;
  }
}
</style>

<script>
import { ref } from 'vue'

export default {
  setup(){
    const rightDrawerOpen = ref(false)

    return {
      rightDrawerOpen,
    }
  },
}
</script>