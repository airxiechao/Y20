<template>
  <span class="gt-xs q-mr-xs">
    <router-link to="/user/account" class="text-dark" style="text-decoration: none;">{{ username }}</router-link>
  </span>
  <q-btn class="btn-user-menu" flat round dense size="lg" icon="account_circle">
    <q-menu anchor="bottom right" self="top right">
      <template v-if="username">
        <q-list style="min-width: 150px;">
          <q-item-label header class="bg-grey-2 text-dark text-h6 lt-sm">
            {{ username }}
          </q-item-label>
          <q-separator />

          <q-item >
            <q-item-section>
              <TeamSelect outlined bg-color="white" />
            </q-item-section>
          </q-item>
          <q-separator />
          
          <q-item clickable v-close-popup to='/workspace/project'>
            <!-- <q-item-section avatar>
              <q-icon name="grid_view" size="sm" />
            </q-item-section> -->
            <q-item-section>项目</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to='/workspace/agent'>
            <!-- <q-item-section avatar>
              <q-icon name="computer" size="sm" />
            </q-item-section> -->
            <q-item-section>节点</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to='/workspace/template'>
            <!-- <q-item-section avatar>
              <q-icon name="category" size="sm" />
            </q-item-section> -->
            <q-item-section>应用</q-item-section>
          </q-item>
          <q-item clickable v-close-popup tag="a" href='/docs' target="_blank">
            <q-item-section>文档</q-item-section>
          </q-item>

          <q-separator />

          <q-item clickable v-close-popup to='/user/account'>
            <!-- <q-item-section avatar>
              <q-icon name="person_outline" size="sm" />
            </q-item-section> -->
            <q-item-section>账号</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to='/user/team'>
            <!-- <q-item-section avatar>
              <q-icon name="people_outline" size="sm" />
            </q-item-section> -->
            <q-item-section>团队</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to='/user/session'>
            <!-- <q-item-section avatar>
              <q-icon name="login" size="sm" />
            </q-item-section> -->
            <q-item-section>会话</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to='/user/token'>
            <!-- <q-item-section avatar>
              <q-icon name="lock_open" size="sm" />
            </q-item-section> -->
            <q-item-section>令牌</q-item-section>
          </q-item>
          <q-item v-if="ENABLE_NAV_QUOTA" clickable v-close-popup to='/user/billing'>
            <!-- <q-item-section avatar>
              <q-icon name="bar_chart" size="sm" />
            </q-item-section> -->
            <q-item-section>配额</q-item-section>
          </q-item>

          <q-separator />

          <q-item clickable v-close-popup @click="onClickQuit">
            <q-item-section class="text-negative">退出</q-item-section>
            <q-item-section side>
              <q-icon name="logout" size="sm" color="negative" />
            </q-item-section>
          </q-item>
        </q-list>
      </template>

      <template v-else>
        <q-list style="min-width: 150px;">
          <q-item clickable v-close-popup to="/login">
            <q-item-section>登录</q-item-section>
            <q-item-section side>
              <q-icon name="login" size="sm" />
            </q-item-section>
          </q-item>
        </q-list>
      </template>
    </q-menu>
  </q-btn>
</template>

<style lang="scss">

</style>

<script>
import { inject, computed } from 'vue'
import TeamSelect from '@/components/TeamSelect'
import authApi from '@/api/auth.api'

export default {
  components: {
    TeamSelect,
  },
  setup(){ 
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()
    const username = computed(() => {
      return store.getters.username
    })

    return {
      ENABLE_NAV_QUOTA: ENABLE_NAV_QUOTA,

      username,

      onClickQuit(){
        authApi.logout().then(resp => {
          store.commit('clearLogin')
          window.location.href = "/"
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      }, 
    }
  },
}
</script>