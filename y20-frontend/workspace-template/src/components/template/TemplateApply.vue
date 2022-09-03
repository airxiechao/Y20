<template>
  <LayoutTwoColumn class="template-apply">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="返回" @click="onClickBack" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md relative-position">
            <div class="q-pb-md">
              <div>创建流水线</div>
              <q-skeleton v-if="loading" type="text" width="15%" />
              <q-breadcrumbs v-else class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el v-if="templateName" :label="`${templateUsername}/${templateName}`" :to="`/workspace/template/${templateId}`"/>
              </q-breadcrumbs>
            </div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-select
                outlined
                v-model="toProjectId"
                :options="projects" 
                :label="`${$t('label-project')} *`"
                :hint="$t('error-no-project')"
                emit-value 
                map-options 
                @filter="onSelectProject"
                lazy-rules
                :rules="[ 
                    val => val !== null || $t('error-no-project'),
                ]"
              />

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
                :loading="loading"
              />

              <div>
                <q-checkbox v-model="flagOneRun" :label="$t('label-one-run')" />
              </div>
              
              <div>
                <q-toggle v-model="flagRunAfterApply" :label="$t('label-run-after-apply')" />
              </div>
              
              <div class="q-mt-md">
                <q-btn unelevated :label="$t('label-apply')" type="submit" color="primary" :loading="applyLoading"/>
                <q-btn flat class="q-ml-sm" label="取消" @click="onClickBack" />
              </div>
            </q-form>
            <!-- <q-inner-loading :showing="loading">
              <q-spinner-gears size="30px" color="primary" />
            </q-inner-loading> -->
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.template-apply{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "流水线名称",
    "error-no-name": "请输入流水线名称",
    "label-project": "项目",
    "error-no-project": "请选择项目",
    "error-name-too-long": "流水线名称长度不超过50个字符",
    "label-one-run": "同时只允许启动一个运行实例",
    "label-run-after-apply": "创建后进入启动页面",
    "label-apply": "创建",
  }
}
</i18n>

<script>
import dayjs from 'dayjs'

import { LayoutTwoColumn } from 'common'
import { ref, inject, onMounted } from 'vue'
import projectApi from '@/api/project.api'
import templateApi from '@/api/template.api'

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
    const templateName = ref('')
    const templateUsername = ref('')

    const projects = ref([])
    const toProjectId = ref(null)
    const name = ref(null)
    const flagOneRun = ref(true)
    const flagRunAfterApply = ref(false)
    const loading = ref(false)
    const applyLoading = ref(false)

    const searchProject = (inputValue, doneFn, abortFn) => {
      projectApi.list({ 
        name: inputValue
       }).then(resp => {
        projects.value = resp.data.page.map(a => {
          return {
            label: a.name,
            value: a.projectId,
          }
        })

        if(doneFn){
          doneFn()
        }
      }, resp => {
        if(abortFn){
          abortFn()
        }
      })
    }

    onMounted(() => {
      loading.value = true
      templateApi.getBasic({ templateId }).then(resp => {
        const data = resp.data
        templateName.value = data.name
        templateUsername.value = data.username
        name.value = `${data.name}`//+dayjs().format('YYYYMMDDHHmmss')
      
        store.commit('setTitle', { title: `${templateUsername.value}/${templateName.value}` })
      }).finally(() => {
        loading.value = false
      })
    })

    return {
      templateId,
      templateName,
      templateUsername,
      projects,

      name,
      toProjectId,
      flagOneRun,
      flagRunAfterApply,
      loading,
      applyLoading,

      onSelectProject(inputValue, doneFn, abortFn){
        searchProject(inputValue, doneFn, abortFn)
      },

      onClickBack(){
        router.back()
      },

      onSubmit(){
        applyLoading.value = true
        templateApi.apply({
          templateId,
          toProjectId: toProjectId.value,
          name: name.value,
          flagOneRun: flagOneRun.value,
        }).then(resp => {
          qUtil.notifySuccess('创建流水线完成')
          const pipelineId = resp.data
          if(flagRunAfterApply.value){
            router.push({ 
              path: `/project/${toProjectId.value}/pipeline/${pipelineId}/run/create`,
            })
          }else{
            router.push({ 
              path: `/project/${toProjectId.value}/pipeline/${pipelineId}/step`,
            })
          }
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(()=>{
          applyLoading.value = false
        })
      },
    }
  }
};
</script>