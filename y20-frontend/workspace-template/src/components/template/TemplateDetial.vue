<template>
  <LayoutTwoColumn class="template-detail">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="应用市场" to="/workspace/template" />
            <q-toolbar-title></q-toolbar-title>
            <q-btn unelevated class="q-ml-md" icon="add" color="primary" :to="`/workspace/template/${templateId}/apply`" label="创建流水线" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content relative-position">
          <template v-if="loading">
            <q-card flat>
              <div class="q-pa-md">
                <q-item-section>
                  <q-item-label>
                    <q-skeleton type="text" style="width: 25%;" />
                  </q-item-label>
                  <q-item-label caption>
                    <q-skeleton type="text" style="width: 20%;" />
                  </q-item-label>
                </q-item-section>
              </div>
              <div class="q-pa-md">
                <q-skeleton type="text" />
                <q-skeleton type="text" />
                <q-skeleton type="text" width="50%" />
              </div>
            </q-card>
          </template>
          <template v-else>
            <q-card flat class="template-detail-card">
              <div class="q-pa-md page-heading">
                <template v-if="template.name">
                  <div>
                    {{template.username}}/<a :href="`/nav/workspace/template/${templateId}`">{{template.name}}</a>
                  </div>
                  <q-breadcrumbs class="text-subtitle2 text-grey" active-color="grey">
                    <q-breadcrumbs-el :label="`更新于 ${dayjs(template.lastUpdateTime || template.createTime).format('YYYY-MM-DD HH:mm:ss')}`"/>
                  </q-breadcrumbs>
                </template>
                <template v-else>
                  <q-skeleton type="text" style="width: 25%;" />
                </template>
              </div>
              
              <q-tabs
                align="justify"
                v-model="tab"
                active-color="primary"
                narrow-indicator
              >
                <q-tab name="description" label="描述" />
                <q-tab name="step" label="步骤" />
                <q-tab name="variable" label="变量" />
                <q-tab name="file" label="文件" />
              </q-tabs>
              <q-separator />
              <div class="q-pa-md">
                <router-view />
              </div>
            </q-card>
          </template>

          <!-- <q-inner-loading :showing="loading">
            <q-spinner-gears size="30px" color="primary" />
          </q-inner-loading> -->
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/pipeline-template.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何发布流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

          <q-item clickable tag="a" href="/docs/guide/pipeline-run.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何运行流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

          <q-item clickable tag="a" href="/docs/guide/agent-join.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何接入新节点？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.template-detail{
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
import dayjs from 'dayjs'
import { LayoutTwoColumn } from 'common'
import { ref, inject, provide, watch, onMounted } from 'vue'

import templateApi from '@/api/template.api'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const { templateId } = route.params
    const template = ref({})
    const loading = ref(false)

    provide('template', template)

    const pathToken = route.path.split('/')
    const tab = ref(
      pathToken.length > 4  ? pathToken[4] : 'description'
    )
    watch(tab, (tab) => {
      router.push(`/workspace/template/${templateId}/${tab}`)
    })

    onMounted(() => {
      loading.value = true
      let templateData
      templateApi.getFull({ templateId }).then(resp => {
        templateData = resp.data
        store.commit('setTitle', { title: `${templateData.username}/${templateData.name}` })
        pipelineApi.listStepType().then(resp => {
          let stepTypeNames = {}
          const stepTypeData = resp.data
          if(stepTypeData){
            stepTypeData.forEach(t => {
              stepTypeNames[t.type] = t.name
            })
          }
          
          let steps = templateData.steps
          if(steps){
            steps.forEach(s => {
              s.typeName = stepTypeNames[s.type]
            })
          }

          if(!templateData.description){
            templateData.description = '> 没有描述'
          }

          template.value = templateData
        })
      }).finally(() => {
        loading.value = false
      })
    })

    return {
      dayjs,
      tab,
      templateId,
      template,
      loading,

      onClickCreatePipeline(){
        router.push(`/workspace/template/${templateId}/apply`)
      },

    }
  }
};
</script>