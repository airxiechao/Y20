<template>
  <LayoutTwoColumn class="join-agent">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="节点" to="/workspace/agent" />
            <q-toolbar-title></q-toolbar-title>
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-sm">
            <q-banner dense rounded class="q-pa-md bg-grey-2">
              <template v-slot:avatar>
                <q-icon name="info" color="primary" />
              </template>
              通过以下方法安装节点程序，安装包大小为90M左右。
              节点程序使用Java编写，以服务（y20-agent-client）的方式运行。
              节点和服务器的通信是加密的。
            </q-banner>
            <div>
              <q-card flat class="join-agent-card q-mt-md">
                <q-tabs
                  align="justify"
                  v-model="tab"
                  active-color="primary"
                  narrow-indicator
                >
                  <q-tab name="script" label="脚本接入" />
                  <q-tab name="manual" label="手动接入" />
                </q-tabs>
                <q-separator />
                <div class="q-pa-md">
                  <router-view />
                </div>
              </q-card>
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
.join-agent{
  &-card{
  }
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, watch } from 'vue'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()

    const { projectId } = route.params

    const pathToken = route.path.split('/')
    const tab = ref(
      pathToken.length > 4  ? pathToken[4] : 'script'
    )

    watch(tab, (tab) => {
      router.push(`/workspace/agent/join/${tab}`)
    })

    return {
      tab,
    }
  }
};
</script>