<template>
  <div class="pipeline-config">
    <div class="page-heading text-primary q-pb-md">基础设置</div>
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

      <q-field
        borderless
        stack-label
        bg-color="white"
        v-model="description"
        :label="`${$t('label-description')}`"
        :hint="$t('error-description-too-long')"
        lazy-rules
        :rules="[ 
            val => !val || val.length < 5000 || $t('error-description-too-long')
        ]"
      >
        <div class="full-width">
          <div class="row ">
            <div :class="{
              'col-12': true, 
              'col-sm-6': flagPreview,
            }">
              <CodeEditor class="description-editor" v-model="description"/>
            </div>
            <div v-if="flagPreview" :class="{
              'col-12': true, 
              'col-sm-6': flagPreview,
            }">
              <MarkdownViewer class="description-preview" :source="description" :style="{
                'border-left-width': $q.screen.gt.xs && flagPreview ? '0' : '1px',
              }" />
            </div>
          </div>
          <div class="q-pt-xs">
            <q-toggle class="text-grey-7" size="xs" v-model="flagPreview" label="预览"/>
          </div>
        </div>
       
        
      </q-field>

      <!-- <q-input
        outlined
        type="textarea"
        v-model="description"
        :label="`${$t('label-description')}`"
        :hint="$t('error-description-too-long')"
        lazy-rules
        :rules="[ 
            val => val.length < 5000 || $t('error-description-too-long')
        ]"
      /> -->

      <div>
        <q-checkbox v-model="flagOneRun" :label="$t('label-one-run')" />
      </div>

      <div>
        <q-checkbox v-model="flagInjectProjectVariable" :label="$t('label-inject-project-variable')" />
      </div>

      <div class="text-primary">定时设置</div>

      <q-checkbox v-model="flagCronEnabled" :label="$t('label-cron-enabled')" />

      <template v-if="flagCronEnabled">
        <q-input outlined v-model="cronBeginTime" :label="`${$t('label-cron-begin-time')} *`"
          lazy-rules
          :rules="[ 
              val => !flagCronEnabled || !!val || $t('error-no-cron-begin-time'),
              val => dayjs(val, 'YYYY-MM-DD HH:mm:ss').isValid() || $t('error-cron-begin-time'),
          ]"
        >
          <template v-slot:prepend>
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy transition-show="scale" transition-hide="scale">
                <q-date v-model="cronBeginTime" mask="YYYY-MM-DD HH:mm:ss">
                  <div class="row items-center justify-end">
                    <q-btn v-close-popup label="关闭" color="primary" flat />
                  </div>
                </q-date>
              </q-popup-proxy>
            </q-icon>
          </template>

          <template v-slot:append>
            <q-icon name="access_time" class="cursor-pointer">
              <q-popup-proxy transition-show="scale" transition-hide="scale">
                <q-time v-model="cronBeginTime" mask="YYYY-MM-DD HH:mm:ss" format24h>
                  <div class="row items-center justify-end">
                    <q-btn v-close-popup label="关闭" color="primary" flat />
                  </div>
                </q-time>
              </q-popup-proxy>
            </q-icon>
          </template>
        </q-input>

        <q-field stack-label borderless v-model="cronIntervalSecs" :label="`${$t('label-cron-interval-secs')} *`"
          lazy-rules
          :rules="[ 
              val => !flagCronEnabled || !!val || $t('error-no-cron-interval-secs'),
              val => val > 0 && parseInt(val) == val || $t('error-cron-interval-secs'),
          ]"
        >
          <DelayTimeInput  v-model="cronIntervalSecs" />
        </q-field>

        <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" />

      </template>

      <div>
        <q-btn unelevated :label="$t('label-save')" :loading="flagSaveLoading" type="submit" color="primary"/>
      </div>
    </q-form>
  </div>
</template>

<style lang="scss">
.pipeline-config{

  .description-preview{
    height: 302px;
    border: 1px solid #ccc;
    overflow: auto;
    background: #2b2b2b;
    color: #f5f5f5;

    table {
      td, th {
        background: #2b2b2b;
      }
    }
  }
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "名称",
    "error-no-name": "请输入流水线名称",
    "error-name-too-long": "名称长度不超过100个字符",
    "label-description": "描述",
    "error-no-description": "请输入流水线描述",
    "error-description-too-long": "描述长度不超过5000个字符。支持Markdown",
    "label-save": "保存",
    "label-one-run": "同时只允许启动一个运行实例",
    "label-inject-project-variable": "自动注入项目变量",
    "label-cron-enabled": "允许定时启动",
    "label-cron-begin-time": "开始时间",
    "label-cron-interval-secs": "间隔时间",
    "error-no-cron-begin-time": "请输入定时开始时间",
    "error-cron-begin-time": "定时开始时间格式为：YYYY-MM-DD HH:mm:ss",
    "error-no-cron-interval-secs": "请输入定时间隔时间",
    "error-cron-interval-secs": "定时间隔时间必须是大于或等于1的整数",
  }
}
</i18n>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import customParseFormat from 'dayjs/plugin/customParseFormat'
dayjs.extend(duration)
dayjs.extend(customParseFormat)

import { ref, inject, watch, computed, onMounted } from 'vue'

import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'
import DelayTimeInput from '@/components/pipeline/common/DelayTimeInput'
import CodeEditor from '@/components/pipeline/codeeditor/CodeEditor'
import MarkdownViewer from '@/components/pipeline/markdown/MarkdownViewer'

import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    PipelineInVariablesForm,
    DelayTimeInput,
    CodeEditor,
    MarkdownViewer,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()

    const { projectId, pipelineId } = route.params

    const pipelineName = inject('pipelineName')
    const pipelineInVariables = ref([])

    const name = ref(null)
    const description = ref(null)
    const flagOneRun = ref(null)
    const flagInjectProjectVariable = ref(null)
    const flagCronEnabled = ref(null)
    const cronBeginTime = ref(null)
    const cronIntervalSecs = ref(null)

    const cronInParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })

    const pipeline = computed(() => {
      return store.state.project.pipeline.pipeline
    })

    const flagSaveLoading = ref(false)
    const flagPreview = ref(true)
    
    const pipelineReady = ref(false)
    const variableReady = ref(false)
    const ready = computed(() => {
      return pipelineReady.value && variableReady.value
    })

    const init = () => {
      const data = pipeline.value
      name.value = data.name
      description.value = data.description
      flagOneRun.value = data.flagOneRun
      flagInjectProjectVariable.value = data.flagInjectProjectVariable
      flagCronEnabled.value = data.flagCronEnabled
      cronBeginTime.value = dayjs(data.cronBeginTime).format('YYYY-MM-DD HH:mm:ss')
      cronIntervalSecs.value = data.cronIntervalSecs
      const cronInParams = data.cronInParams
      if(cronInParams){
        Object.keys(cronInParams).forEach(k => {
          const i = pipelineInVariables.value.findIndex(v => v.name === k)
          if(i >= 0){
            pipelineInVariables.value[i].value = cronInParams[k]
          }
        })
      }
    }

    watch(pipeline, () => {
      pipelineReady.value = true
      init()
    })

    watch(ready, () => {
      init()
    })

    watch(flagCronEnabled, (newValue) =>{
      if(!newValue){
        cronBeginTime.value = null
        cronIntervalSecs.value = null
        pipelineInVariables.value.forEach(v => {
          v.value = v.defaultValue || null
        })
      }
    })

    onMounted(() => {
      pipelineApi.listVariable({ pipelineId }).then(resp => {
        pipelineInVariables.value = Object.values(resp.data)
          .sort((a,b) => (a.order || 0) - (b.order || 0))
          .filter(v => v.kind == 'IN')
        
        variableReady.value = true
        if(pipeline.value){
          pipelineReady.value = true
        }
      })
    })

    return {
      $q,
      dayjs,
      pipelineInVariables,
      name,
      description,
      flagOneRun,
      flagInjectProjectVariable,
      flagCronEnabled,
      cronBeginTime,
      cronIntervalSecs,
      cronInParams,

      flagSaveLoading,
      flagPreview,

      onSubmit(){
        flagSaveLoading.value = true
        pipelineApi.updateBasic({
          pipelineId,
          name: name.value,
          description: description.value,
          flagOneRun: flagOneRun.value,
          flagInjectProjectVariable: flagInjectProjectVariable.value,
          flagCronEnabled: flagCronEnabled.value,
          cronBeginTime: cronBeginTime.value,
          cronIntervalSecs: cronIntervalSecs.value,
          cronInParams: cronInParams.value,
        }).then(resp => {
          qUtil.notifySuccess('保存完成')
          pipelineName.value = name.value
          store.commit('setTitle', { title: pipelineName.value })

          pipelineApi.getBasic({ pipelineId }).then(resp => {
            store.commit('project/pipeline/setPipeline', resp.data)
          })
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(()=>{
          flagSaveLoading.value = false
        })
      },
    }
  },
}
</script>