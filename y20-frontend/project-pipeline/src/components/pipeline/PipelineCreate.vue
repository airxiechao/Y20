<template>
  <LayoutTwoColumn class="pipeline-create">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="流水线" :to="`/project/${projectId}/pipeline`" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md">创建新流水线</div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="name"
                :label="`${$t('label-name')} *`"
                :hint="$t('error-name-too-long')"
                lazy-rules
                :rules="[ 
                    val => val && val.length > 0 || $t('error-no-name'),
                    val => val.length < 100 || $t('error-name-too-long')
                ]"
              />

              <q-checkbox v-model="flagOneRun" :label="$t('label-one-run')" />

              <div>
                <q-btn unelevated :label="$t('label-create')" :loading="flagCreateLoading" type="submit" color="primary"/>
              </div>
            </q-form>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.pipeline-create{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "名称",
    "error-no-name": "请输入流水线名称",
    "error-name-too-long": "名称长度不超过100个字符",
    "label-create": "创建",
    "label-one-run": "同时只允许启动一个运行实例",
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed } from 'vue'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const { projectId } = route.params
    const projectName = inject('projectName')

    const name = ref(null)
    const flagOneRun = ref(true)

    return {
      projectId,
      projectName,

      name,
      flagOneRun,

      onClickBack(){
        router.back()
      },
      onSubmit(){
        pipelineApi.create({
          name: name.value,
          flagOneRun: flagOneRun.value,
        }).then(resp => {
          const pipelineId = resp.data
          router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
        }, resp => {
          qUtil.notifyError(resp.message)
        })
      },
    }
  }
};
</script>