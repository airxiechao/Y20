<template>
  <LayoutTwoColumn class="pipeline-run-create">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="返回" @click="onClickBack" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">
              <div>启动流水线</div>
              <q-skeleton v-if="!pipelineName" type="text" animation="fade" style="max-width: 150px;" />
              <q-breadcrumbs v-else class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el :label="`${pipelineName}`" :to="`/project/${projectId}/pipeline/${pipelineId}`"/>
              </q-breadcrumbs>
            </div>
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
                    val => val.length < 50 || $t('error-name-too-long')
                ]"
              />
              
              <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" :loading="loading" />

              <div class="q-gutter-x-sm">
                <q-toggle size="xs" class="q-ml-none" v-model="flagDebug" label="调试模式" />
                <q-toggle size="xs" v-model="flagTail" label="跟随日志" />
              </div>

              <div class="q-mt-md">
                <q-btn unelevated :label="$t('label-create')" type="submit" color="primary" :loading="createRunLoading"/>
                <q-btn flat class="q-ml-sm bg-grey-2" label="取消" @click="onClickBack" /> 
              </div>
            </q-form>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/pipeline-run.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何运行流水线？</q-item-section>
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
.pipeline-run-create{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "执行名称",
    "error-no-name": "请输入流水线运行名称",
    "error-name-too-long": "名称长度不超过50个字符",
    "label-create": "启动",
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'

import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    LayoutTwoColumn,
    PipelineInVariablesForm,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId } = route.params

    const createRunLoading = ref(false)
    const pipelineInVariables = ref([])
    const name = ref(dayjs().format('执行 YYYYMMDDHHmmss'))
    const flagDebug = ref(false)
    const flagTail = ref(false)
    const loading = ref(false)

    const inParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })
    
    onMounted(() => {
      loading.value = true
      pipelineApi.listVariable({ pipelineId }).then(resp => {
        pipelineInVariables.value = Object.values(resp.data).sort((a,b) => (a.order || 0) - (b.order || 0)).filter(v => v.kind == 'IN')
        pipelineInVariables.value.forEach(v => {
          v.value = v.defaultValue || null
        })
      }).finally(() => {
        loading.value = false
      })
    })

    return {
      projectId,
      pipelineId,

      createRunLoading,
      pipelineName,
      pipelineInVariables,
      name,
      flagDebug,
      flagTail,
      loading,

      onClickBack(){
        router.back()
      },

      onSubmit(){
        createRunLoading.value = true
        pipelineApi.createRun({
          pipelineId,
          name: name.value,
          inParams: inParams.value,
          flagDebug: flagDebug.value
        }).then(resp => {
          const pipelineRunId = resp.data
          router.push({ 
            path: `/project/${projectId}/pipeline/${pipelineId}/run/${pipelineRunId}`,
            query: {
              start: true,
              tail: flagTail.value,
            }  
          })
        }, resp => {
          const code = resp.code
          switch(code){
            case 'ERROR_ONLY_ONE_RUN':
              $q.dialog({
                title: '已放入等待队列',
                message: `流水线 ${pipelineName.value} 同时只允许启动一个运行实例`,
                cancel: '关闭',
                ok: '确定',
                persistent: true
              }).onOk(() => {
                router.push(`/project/${projectId}/pipeline/${pipelineId}/pending`)
              })
              break;
            case 'ERROR_NO_ENOUGH_QUOTA':
              $q.dialog({
                title: '配额不足',
                message: `流水线 ${pipelineName.value} 无法启动，请购买配额`,
                cancel: '取消',
                ok: '前往',
                persistent: true
              }).onOk(() => {
                router.push(`/user/billing`)
              })
              break;
            default:
              qUtil.notifyError(resp.message)
              break;
          }
        }).finally(()=>{
          createRunLoading.value = false
        })
      },
    }
  }
};
</script>