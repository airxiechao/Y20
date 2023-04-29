<template>
  <q-layout view="hHh LpR fFf" class="layout-nav-secondary">
    <q-header :class="{
      'header': true,
      'wide': $q.screen.gt.sm,
    }">
      <q-toolbar class="text-dark">
        <q-btn flat round dense icon="menu" @click="toggleLeftDrawerOpen" />
        <q-toolbar-title>
          <a class="text-dark text-bold" href="/nav/workspace">
            <q-icon class="q-mr-sm vertical-middle" size="md" :name="`img:/${FRONTEND_SERVICE_NAME}/static/img/icon-logo.png`" />
            <span class="vertical-middle">鲲擎流水线</span>
          </a>
          <div class="vertical-middle text-subtitle2 text-grey q-ml-md gt-xs" style="display: inline-block;">
            <slot name="header"></slot>
          </div>
        </q-toolbar-title>
        <BtnUserMenu />
      </q-toolbar>
    </q-header>

    <q-drawer width="250" class="drawer" show-if-above :mini="leftDrawerMini" v-model="leftDrawerOpen" side="left">
      <slot name="left"></slot>
    </q-drawer>

    <q-page-container :class="{
      'container': true,
      'wide': $q.screen.gt.sm
    }">
      <slot name="center"></slot>
    </q-page-container>
  </q-layout>
  
</template>

<style lang="scss">
@import '@/assets/scss/var';

.layout-nav-secondary{

  .header {
    background: #fff;
    border-bottom: 1px solid #ddd;

    &.wide {
      
    }
  }

  .drawer{
  }

  .container{
    background: $--color-page-bg;
    // padding-bottom: 8px;
    
    &.wide {
      .btn-user-menu{
        margin: 15px;
      }
    }
  }
}
</style>

<script>
import { ref, inject, computed } from 'vue'
import { useQuasar } from 'quasar'
import BtnUserMenu from '@/components/BtnUserMenu'

export default {
  components: {
    BtnUserMenu
  },
  setup(){
    const $q = useQuasar()
    const store = inject('useStore')()

    const username = computed(() => {
      return store.getters.username
    })

    const projectName = computed(() => {
      return store.state.project.name 
    })
    
    const leftDrawerOpen = ref(false)
    const leftDrawerMini = ref(!username.value)

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,

      leftDrawerOpen,
      leftDrawerMini,

      projectName,
      
      toggleLeftDrawerOpen() {
        if($q.screen.gt.sm){
          leftDrawerMini.value = !leftDrawerMini.value
        }else{
          leftDrawerOpen.value = !leftDrawerOpen.value
        }
        
      },

      toggleLeftDrawerMini() {
        leftDrawerMini.value = !leftDrawerMini.value
      },
    }
  },
}
</script>