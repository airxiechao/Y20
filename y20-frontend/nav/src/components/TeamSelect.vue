<template>
  <q-select
    borderless
    dense
    map-options
    emit-value
    v-model="teamId"
    :options="joinedTeams"
    :loading="teamLoading"
  />
</template>

<script>
import { ref, inject, computed, onMounted, watch } from 'vue'
import authApi from '@/api/auth.api'

export default {
  setup(){
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const teamLoading = ref(false)
    const joinedTeams = ref([
      {
        label: '我的团队',
        value: null,
      },
    ])

    const username = computed(() => {
      return store.getters.username
    })

    const teamId = computed({
      get(){
        return store.state.workspace.teamId
      },
      set(val){
        store.commit('workspace/setTeamId', { 
          teamId: val
        })
      }
    })

    watch(teamId, () => {
      let currentRoute = {...route}
      router.replace('/refresh')
      setTimeout(() => {
        const tokens = currentRoute.path.split('/')
        if(tokens[1] == 'workspace'){
          router.replace(`/workspace/${tokens[2] || 'project'}`)
        }else{
          router.replace('/workspace/project')
        }
        
      }, 100)
    })

    onMounted(() => {
      if(username.value){
        teamLoading.value = true
        authApi.listJoinedTeam().then(resp => {
          const data = resp.data.page
          joinedTeams.value.push(...data.map(team => {
            return {
              label: team.username + ' 团队',
              value: team.teamId
            }
          }))
        }).finally(() => {
          teamLoading.value = false
        })
      }
    })

    return {
      username,
      joinedTeams,
      teamId,
      teamLoading,
    }
  }
}
</script>