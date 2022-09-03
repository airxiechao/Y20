<template>
  <LayoutTwoColumn class="pipeline-publish">
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
              <div>发布到应用市场</div>
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

              <q-select
                outlined
                clearable
                use-input
                v-model="templateId"
                :options="templates" 
                :label="`${$t('label-template-update')}`"
                :hint="$t('hint-template-update')"
                emit-value 
                map-options 
                @filter="onSelectTemplate"
              />
              
              <div class="q-mt-md">
                <q-btn unelevated :label="$t('label-publish')" type="submit" color="primary" :loading="publishLoading"/>
                <q-btn class="q-ml-sm" flat label="取消" @click="onClickBack" /> 
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
      <div class="text-grey q-mb-md">快捷操作</div>
      <q-list>
        <q-item clickable tag="a" to="/workspace/template">
          <q-item-section avatar>
            <q-icon name="shortcut" />
          </q-item-section>
          <q-item-section>浏览应用市场</q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>
      </q-list>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.pipeline-publish{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "名称",
    "error-no-name": "请输入应用名称",
    "error-name-too-long": "名称长度不超过50个字符",
    "label-template-update": "覆盖已发布应用",
    "hint-template-update": "如果要覆盖已发布的应用，请选择。否则为空",
    "label-publish": "发布",
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'
import pipelineApi from '@/api/pipeline.api'
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
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId } = route.params

    const name = ref(pipelineName.value)
    const templateId = ref(null)
    const templates = ref([])
    const loading = ref(false)
    const publishLoading = ref(false)

    const pipeline = computed(() => {
      return store.state.project.pipeline.pipeline
    })

    const pipelineReady = ref(false)
    const templateReady = ref(false)
    const ready = computed(() => {
      return pipelineReady.value && templateReady.value
    })

    const init = () => {
      for(let i = 0; i < templates.value.length; ++i){
        const template = templates.value[i]
        if(template.value == pipeline.value.publishedTemplateId){
          templateId.value = pipeline.value.publishedTemplateId
          break
        }
      }
    }
    
    watch(pipelineName, (newValue) => {
      name.value = newValue
    })

    watch(pipeline, () => {
      pipelineReady.value = true
      init()
    })

    watch(ready, () => {
      init()
    })

    const searchTemplate = (inputValue, doneFn, abortFn) => {
      templateApi.listMy({ 
        name: inputValue
       }).then(resp => {
        templates.value = resp.data.page.map(a => {
          return {
            label: a.name,
            value: a.templateId,
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
      searchTemplate('', () => {
        templateReady.value = true
        if(pipeline.value){
          pipelineReady.value = true
        }
      })
    })

    return {
      projectId,
      pipelineName,
      pipelineId,

      name,
      templateId,
      templates,
      loading,
      publishLoading,

      onClickBack(){
        router.back()
      },

      onSelectTemplate(inputValue, doneFn, abortFn){
        searchTemplate(inputValue, doneFn, abortFn)
      },

      onSubmit(){
        publishLoading.value = true
        pipelineApi.publish({
          projectId,
          pipelineId,
          name: name.value,
          templateId: templateId.value,
        }).then(resp => {
          qUtil.notifySuccess('发布完成')
          const templateId = resp.data
          router.push({ 
            path: `/workspace/template/${templateId}`,
          })
        }, resp => {
          qUtil.notifyError(resp.message)
        }).finally(()=>{
          publishLoading.value = false
        })
      },
    }
  }
};
</script>