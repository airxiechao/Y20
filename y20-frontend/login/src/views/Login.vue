<template>
  <LayoutThreeColumn class="page-login q-pa-md">
    <template v-slot:center>
      <div class="page-login-center full-height flex justify-center">
        <div class="page-login-card full-width self-center">
           <div class="text-center text-h5 q-mb-lg">
            <a href="/">
              <q-icon class="q-mr-sm vertical-middle" size="4rem" :name="`img:/icon-logo.png`" />
              <span class="vertical-middle text-black text-weight-bold">鲲擎运维</span>
            </a>
          </div>

          <div class="text-center text-h6"><router-link class="text-dark" to="/login" style="text-decoration: none;">登录</router-link></div>
          <div class="text-center text-subtitle text-grey q-mb-md">欢迎回来</div>

          <router-view />

          <div class="q-mt-lg text-center text-grey">还没有账号？<q-btn color="primary" dense flat label="注册" type="a" to="/signup" /></div>
        </div>
      </div>
    </template>
    <template v-slot:right>

    </template>
    
  </LayoutThreeColumn>
</template>

<style lang="scss">
.page-login {
  height: 100%;
  background-color: #f5f5f5;
  background-repeat: no-repeat;
  background-position-x: 20%;
  background-position-y: 200px;
  overflow: auto;

  &-center {
    flex-direction: column;
  }

  &-card {
    max-width: 360px;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 5px 10px #ddd;

    .tab{
      padding: 20px 0px 0px 0px;
    }
  }
}
</style>

<i18n>
{
  "zhCN": {
    "title-login": "登录",
  }
}
</i18n>

<script>
import { LayoutThreeColumn } from "common"
import { ref, inject, onMounted } from 'vue'

export default {
  components: {
    LayoutThreeColumn,
  },
  setup () {
    const router = inject('useRouter')()

    onMounted(() => {
      store.commit('setTitle', { title: '登录' })
    })

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,
    }
  }
};
</script>