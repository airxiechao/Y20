<template>
  <LayoutNavSecondary class="user">
    <template v-slot:header>
      <q-breadcrumbs class="text-dark">
        <q-breadcrumbs-el tag="a" label="工作空间" to="/workspace/project" />
        <q-breadcrumbs-el tag="a" label="用户" to="/user/account" />
      </q-breadcrumbs>
    </template>

    <template v-slot:left>
      <q-list>
        <q-item-label header>
          <span class="text-dark text-bold">用户</span>
        </q-item-label>

        <q-item
          clickable
          v-ripple
          :active="link == 'account'"
          active-class="drawer-menu-active"
          to='/user/account'
        >
          <q-item-section avatar>
            <q-icon name="person_outline" />
          </q-item-section>

          <q-item-section>
            账号
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'team'"
          active-class="drawer-menu-active"
          to='/user/team'
        >
          <q-item-section avatar>
            <q-icon name="people_outline" />
          </q-item-section>

          <q-item-section>
            团队
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'session'"
          active-class="drawer-menu-active"
          to='/user/session'
        >
          <q-item-section avatar>
            <q-icon name="login" />
          </q-item-section>

          <q-item-section>
            会话
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'token'"
          active-class="drawer-menu-active"
          to='/user/token'
        >
          <q-item-section avatar>
            <q-icon name="lock_open" />
          </q-item-section>

          <q-item-section>
            令牌
          </q-item-section>
        </q-item>

        <q-item
          v-if="ENABLE_NAV_QUOTA"
          clickable
          v-ripple
          :active="link == 'billing'"
          active-class="drawer-menu-active"
          to='/user/billing'
        >
          <q-item-section avatar>
            <q-icon name="data_usage" />
          </q-item-section>

          <q-item-section>
            配额
          </q-item-section>
        </q-item>
        
      </q-list>
    </template>
    
    <template v-slot:center>
      <div class="page-container">
        <router-view />
      </div>
    </template>
  </LayoutNavSecondary>
</template>

<style lang="scss" scoped>
.user{
}
</style>

<script>
import { ref, inject, computed } from 'vue'
import LayoutNavSecondary from '@/components/LayoutNavSecondary'

export default {
  components: {
    LayoutNavSecondary,
  },
  setup(){
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    
    const username = computed(() => {
      return store.getters.username
    })

    store.commit('project/setProjectId', { id: null })
    const link = computed(() => {
      return route.path.split('/')[2]
    })

    return {
      ENABLE_NAV_QUOTA: ENABLE_NAV_QUOTA,
      
      link,
      username,
    }
  }
}
</script>