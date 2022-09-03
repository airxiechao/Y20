<template>
  <LayoutNavSecondary class="workspace">
    <template v-slot:header>
      <q-breadcrumbs class="text-primary">
        <q-breadcrumbs-el>
          <TeamSelect />
        </q-breadcrumbs-el>
        <q-breadcrumbs-el tag="a" label="工作空间" to="/workspace/project" />
      </q-breadcrumbs>
    </template>

    <template v-slot:left>
      <div class="column full-height">
        <q-list class="col">
          <q-item
            clickable
            v-ripple
            :active="link == 'project'"
            active-class="drawer-menu-active"
            to='/workspace/project'
          >
            <q-item-section avatar>
              <q-icon name="grid_view" />
            </q-item-section>

            <q-item-section>
              项目
            </q-item-section>
          </q-item>

          <q-item
            clickable
            v-ripple
            :active="link == 'agent'"
            active-class="drawer-menu-active"
            to='/workspace/agent'
          >
            <q-item-section avatar>
              <q-icon name="computer" />
            </q-item-section>

            <q-item-section>
              节点
            </q-item-section>
          </q-item>

          <q-item
            clickable
            v-ripple
            :active="link == 'template'"
            active-class="drawer-menu-active"
            to='/workspace/template'
          >
            <q-item-section avatar>
              <q-icon name="category" />
            </q-item-section>

            <q-item-section>
              应用
            </q-item-section>
          </q-item>
          
        </q-list>
        <div v-if="username" class="quota-card q-ma-sm">
          <q-list dense>
            <q-item-label header>
              <router-link class="text-grey-5" :to="`/user/billing`">当前配额</router-link>
            </q-item-label>
            <q-item>
              <q-item-section>
                <q-item-label class="text-grey">节点</q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-skeleton v-if="loadingCurrentQuota" type="text" width="20px" />
                <q-item-label v-else class="text-grey">
                  <span :class="{
                    'text-warning' : numAgent > maxAgent,
                  }">
                    {{numAgent}}
                  </span>
                  <q-icon
                    v-if="numAgent > maxAgent"
                    name="warning"
                    size="14px"
                    class="q-ml-xs text-warning"
                  />
                  /
                  <span>{{maxAgent}}</span>
                </q-item-label>
              </q-item-section>
            </q-item>
            <q-item>
              <q-item-section>
                <q-item-label class="text-grey">运行</q-item-label>
              </q-item-section>

              <q-item-section side>
                <q-skeleton v-if="loadingCurrentQuota" type="text" width="20px" />
                <q-item-label v-else class="text-grey">
                  <span :class="{
                    'text-warning' : numPipelineRun >= maxPipelineRun,
                  }">
                    {{numPipelineRun}}
                  </span>
                  <q-icon
                    v-if="numPipelineRun >= maxPipelineRun"
                    name="warning"
                    size="14px"
                    class="q-ml-xs text-warning"
                  />
                  /
                  <span>{{maxPipelineRun}}</span>
                </q-item-label>
                
              </q-item-section>
            </q-item>
          </q-list>
        </div>
      </div>
    </template>
    
    <template v-slot:center>
      <div class="page-container">
        <router-view />
      </div>
    </template>
  </LayoutNavSecondary>
</template>

<style lang="scss" scoped>
.workspace{
  .quota-card{
    
  }
}
</style>

<script>
import { ref, inject, computed, onMounted } from 'vue'
import LayoutNavSecondary from '@/components/LayoutNavSecondary'
import TeamSelect from '@/components/TeamSelect'

import quotaApi from '@/api/quota.api'

export default {
  components: {
    LayoutNavSecondary,
    TeamSelect,
  },
  setup(){
    const $q = inject('useQuasar')()
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    
    store.commit('project/setProjectId', { id: null })
    const link = computed(() => {
      return route.path.split('/')[2]
    })

    const username = computed(() => {
      return store.getters.username
    })

    const numAgent = ref(null)
    const numPipelineRun = ref(null)
    const maxAgent = ref(null)
    const maxPipelineRun = ref(null)
    const quotaBeginTime = ref(null)
    const quotaEndTime = ref(null)
    const loadingCurrentQuota = ref(false)

    const getCurrentQuota = () => {
      loadingCurrentQuota.value = true
      quotaApi.getCurrentQuota().then(resp => {
        const data = resp.data
        quotaBeginTime.value = data.beginTime
        maxAgent.value = data.maxAgent
        maxPipelineRun.value = data.maxPipelineRun

        quotaApi.getQuotaUsage().then(resp => {
          const data = resp.data
          numAgent.value = data.numAgent
          numPipelineRun.value = data.numPipelineRun

          loadingCurrentQuota.value = false
        })
      })
    }

    onMounted(() => {
      if(username.value){
        getCurrentQuota()
      }
    })

    return {
      $q,
      link,
      username,

      numAgent,
      numPipelineRun,
      maxAgent,
      maxPipelineRun,
      quotaBeginTime,
      quotaEndTime,
      loadingCurrentQuota,
    }
  }
}
</script>