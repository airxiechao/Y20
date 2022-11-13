<template>
  <LayoutNavSecondary class="project">
    <template v-slot:header>
      <q-breadcrumbs class="text-dark">
        <q-breadcrumbs-el>
          <TeamSelect />
        </q-breadcrumbs-el>
        <q-breadcrumbs-el tag="a" label="工作空间" to="/workspace/project" />
        <q-breadcrumbs-el tag="a" :label="`${projectName || ''}`" :to="`/project/${projectId}/pipeline`" />
      </q-breadcrumbs>
    </template>
    
    <template v-slot:left>
      <q-list>
        <q-item-label header>
          <span class="text-dark text-bold">项目</span>
        </q-item-label>

        <q-item
          clickable
          v-ripple
          :active="link == 'pipeline'"
          active-class="drawer-menu-active"
          :to="`/project/${projectId}/pipeline`"
        >
          <q-item-section avatar>
            <q-icon name="all_inclusive" />
          </q-item-section>

          <q-item-section>
            流水线
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'monitor'"
          active-class="drawer-menu-active"
          :to="`/project/${projectId}/monitor`"
        >
          <q-item-section avatar>
            <q-icon name="monitor" />
          </q-item-section>

          <q-item-section>
            监视
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'variable'"
          active-class="drawer-menu-active"
          :to="`/project/${projectId}/variable`"
        >
          <q-item-section avatar>
            <q-icon name="superscript" />
          </q-item-section>

          <q-item-section>
            变量
          </q-item-section>
        </q-item>

        <q-item
          clickable
          v-ripple
          :active="link == 'file'"
          active-class="drawer-menu-active"
          :to="`/project/${projectId}/file`"
        >
          <q-item-section avatar>
            <q-icon name="folder_open" />
          </q-item-section>

          <q-item-section>
            文件
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
.project{
}
</style>

<script>
import { ref, inject, computed, onMounted, watch } from 'vue'
import LayoutNavSecondary from '@/components/LayoutNavSecondary'
import TeamSelect from '@/components/TeamSelect'

export default {
  components: {
    LayoutNavSecondary,
    TeamSelect,
  },
  setup(){
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const projectId = route.params.projectId
    const teamLoading = ref(false)

    store.commit('project/setProjectId', { id: projectId })
    const link = computed(() => {
      return route.path.split('/')[3]
    })

    const projectName = computed(() => {
      return store.state.project.name 
    })

    watch(projectName, () => {
      store.commit('setTitle', { title: projectName.value })
    })

    onMounted(() => {
      store.commit('setTitle', { title: projectName.value })
      store.dispatch('project/queryProjectName')
    })

    return {
      link,
      projectId,
      projectName,
    }
  }
}
</script>