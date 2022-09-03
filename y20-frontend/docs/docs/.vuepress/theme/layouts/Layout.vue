<template>
  <Layout>
    <template #navbar-after>
      <a href="/">官网</a>
    </template>
  </Layout>
</template>

<script>
import Layout from '@vuepress/theme-default/lib/client/layouts/Layout.vue'

export default {
  components: {
    Layout,
  },
}
</script>

<style lang="css">
</style>