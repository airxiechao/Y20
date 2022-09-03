<template>
  <div class="text-center q-ma-md">
    <div class="text-red">Error: page not ready</div>
    <div class="q-mt-md">
      <q-btn label="刷新" @click="onClickRefresh" />
    </div>
  </div>
</template>

<style lang="scss">

</style>

<script>
export default {
  setup(){
    return {
      onClickRefresh(){
        window.location.reload()
      }
    }
  }
}
</script>