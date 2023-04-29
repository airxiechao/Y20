<template>
  <LayoutThreeColumn class="page-signup q-pa-md">
    <template v-slot:center>
      <div class="page-signup-center full-height flex justify-center">
        <div class="page-signup-card full-width self-center">
          <div class="text-center text-h5 q-mb-lg">
            <a href="/">
              <q-icon class="q-mr-sm vertical-middle" size="lg" :name="`img:/${FRONTEND_SERVICE_NAME}/static/img/icon-logo.png`" />
              <span class="vertical-middle text-black text-weight-bold">鲲擎流水线</span>
            </a>
          </div>

          <router-view />

        </div>
      </div>
    </template>
    <template v-slot:right>

    </template>
  </LayoutThreeColumn>
</template>

<style lang="scss" scoped>
.page-signup{
  height: 100%;
  background-color: #f5f5f5;
  background-repeat: no-repeat;
  background-position-x: 20%;
  background-position-y: 180px;
  overflow: auto;

  &-card {
    max-width: 450px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 5px 10px #ddd;
    padding: 30px;
  }
}
</style>

<script>
import authApi from '@/api/auth/auth.api'
import { LayoutThreeColumn } from 'common'
import { ref, inject, provide, onMounted } from 'vue'

export default {
  components: {
    LayoutThreeColumn
  },
  setup (props, context) {
    const useQuasar = inject('useQuasar')
    const $q = useQuasar()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()

    onMounted(() => {
      store.commit('setTitle', { title: '注册' })
    })

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,
    }
  }
};
</script>

