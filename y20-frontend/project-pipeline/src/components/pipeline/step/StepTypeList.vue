<template>
  <LayoutTwoColumn class="step-type-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded no-caps flat color="primary" icon="keyboard_backspace" label="步骤列表" 
              :to="`/project/${projectId}/pipeline/${pipelineId}/step`" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card flat bordered class="q-pa-md">
            <div class="q-pb-md">
              <div>选择步骤类型</div>
              <q-skeleton v-if="!pipelineName" type="text" animation="fade" style="max-width: 150px;" />
              <q-breadcrumbs v-else class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el :label="`${pipelineName}`" />
                <q-breadcrumbs-el :label="`第 ${parseInt(stepPosition)+1} 步`" />
              </q-breadcrumbs>
            </div>

            <div class="relative-position">
              <q-card flat>
                <q-tabs
                  align="justify" 
                  v-model="tab" 
                  active-color="primary"
                  narrow-indicator
                >
                  <q-tab name="basic" label="基础" />
                  <q-tab name="tencent" label="腾讯云" />
                </q-tabs>
                <q-separator />
                <q-tab-panels v-model="tab">
                  <q-tab-panel class="q-pa-xs" :name="tabName" v-for="tabName in ['basic', 'tencent']" :key="tabName">
                    <template v-for="category in tabName == 'basic' ? ['环境', '进程', '文件', '控制'] : ['对象存储 COS']" :key="category">
                      <div class="text-primary text-bold q-mb-md q-mt-lg">{{category}}</div>
                      <div class="row q-col-gutter-md relative-position">
                        <div v-if="loading" class="col-12 col-md-6 col-lg-3">
                          <q-card class="full-width column">
                            <q-item>
                              <q-item-section avatar>
                                <q-skeleton type="QAvatar" size="24px" />
                              </q-item-section>

                              <q-item-section>
                                <q-item-label>
                                  <q-skeleton type="text" />
                                </q-item-label>
                              </q-item-section>
                            </q-item>

                            <div class="q-pa-md">
                              <q-skeleton type="text" />
                              <q-skeleton type="text" />
                            </div>

                            <q-card-actions align="center" class="q-gutter-md">
                              <q-skeleton type="QBtn" />
                            </q-card-actions>
                          </q-card>
                        </div>
                        <div v-else class="flex col-12 col-md-6 col-lg-3" 
                          v-for="type in stepTypes.filter(t => t.category == category).sort((l,r) => l.order - r.order)" :key="type.type"
                        >
                          <q-card class="full-width column">
                            <q-card-section class="q-pt-xs col">
                              <q-item>
                                <q-item-section avatar>
                                  <q-icon :name="type.icon || 'sticky_note_2'" />
                                </q-item-section>
                                <q-item-section>
                                  <q-item-label>{{ type.name }}</q-item-label>
                                </q-item-section>
                              </q-item>
                              <div class="text-caption text-grey">
                                {{ type.description || '-' }}
                              </div>
                            </q-card-section>
                            <q-separator />
                            <q-card-actions align="center">
                              <q-btn flat color="primary" icon="add" label="添加" @click="onClickStepType(type.type)" />
                            </q-card-actions>
                          </q-card>
                        </div>
                      </div>
                    </template>
                  </q-tab-panel>
                </q-tab-panels>
              </q-card>
              <!-- <q-inner-loading :showing="loading">
                <q-spinner-gears size="30px" color="primary" />
              </q-inner-loading> -->
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
.step-type-list{
  
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
import { ref, inject, provide, computed, onMounted } from 'vue'
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
    const pipelineName = inject('pipelineName')
    const stepTypes = ref([])
    const loading = ref(false)

    const { projectId, pipelineId, stepPosition} = route.params

    const tab = ref('basic')

    onMounted(() => {
      loading.value = true
      pipelineApi.listStepType().then(resp => {
        stepTypes.value = resp.data
      }, resp => {
        qUtil.notifyError(resp.message || '查询步骤类型发生错误')
      }).finally(() => {
        loading.value = false
      })
    })

    return {
      projectId,
      pipelineId,
      stepPosition,
      pipelineName,
      stepTypes,
      loading,
      tab,

      onClickStepType(type){
        if(stepPosition == 0 && type != 'remote-prepare-env' && type != 'remote-call-pipeline'){
          qUtil.notifyError('流水线的第一个步骤应为“准备环境”或“调用流水线”')
          return
        }

        router.push(`/project/${projectId}/pipeline/${pipelineId}/step/${stepPosition}/add/${type}`)
      },
    }
  }
};
</script>