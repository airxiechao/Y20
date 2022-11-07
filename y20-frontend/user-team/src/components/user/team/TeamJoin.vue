<template>
  <LayoutTwoColumn class="team-join">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded icon="keyboard_backspace" class="bg-grey-3 text-dark" label="团队" to="/user/team" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md bg-white">
            <div class="page-heading">邀请加入团队</div>
            <template v-if="tokenLoading">
              <q-skeleton type="text" animation="fade" />
              <q-skeleton type="text" animation="fade" width="30%" />
            </template>
            <div v-else>
              <span class="text-bold">{{teamJoinToken.username}}</span> 邀请您加入团队！邀请有效期至：{{ dayjs(teamJoinToken.endTime).format('YYYY-MM-DD HH:mm:ss') }}
            </div>
            <div class="q-mt-md">
              <q-btn unelevated color="primary" label="接受" :loading="joinLoading" @click="onClickJoin"/>
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.team-join{
  
}
</style>

<script>
import dayjs from 'dayjs'
import { LayoutTwoColumn } from 'common'
import { ref, inject, onMounted } from 'vue'
import authApi from '@/api/auth.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const joinTokenHashed = new URLSearchParams(window.location.search).get('joinTokenHashed')
    const teamJoinToken = ref({})
    const tokenLoading = ref(false)
    const joinLoading = ref(false)
    
    onMounted(() => {
      tokenLoading.value = true

      if(!joinTokenHashed){
        qUtil.notifyError('邀请链接不正确')
        return
      }

      authApi.getTeamJoinToken({
        joinTokenHashed,
      }).then(resp => {
        const data = resp.data
        teamJoinToken.value = data
        tokenLoading.value = false
      }, resp => {
        qUtil.notifyError('邀请链接已经失效')
      })
    })

    return {
      dayjs,

      teamJoinToken,
      tokenLoading,
      joinLoading,

      onClickJoin(){
        joinLoading.value = true
        authApi.joinTeam({
          joinTokenHashed
        }).then(resp => {
          qUtil.notifySuccess('加入团队完成')
          router.push('/user/team')
        }, resp => {
          qUtil.notifyError(`加入团队发生错误：${resp.message}`)
        }).finally(() => {
          joinLoading.value = false
        })
      },
    }
  }
};
</script>