<template>
  <div class="layout-three-column row">
    <div class="col-left gt-sm"><slot name="left"></slot></div>
    <div class="col-center col"><slot name="center"></slot></div>
    <div class="col-right gt-sm"><slot name="right"></slot></div>
  </div>
</template>

<style lang="scss" scoped>
.layout-three-column{
  .col-left, .col-right {
    width: 300px;
  }
}
</style>