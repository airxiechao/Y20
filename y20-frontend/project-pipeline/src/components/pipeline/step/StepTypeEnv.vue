<template>
  <LayoutTwoColumn class="step-type-env">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded no-caps flat color="primary" icon="keyboard_backspace" 
              :label="isModeEdit?'步骤列表':'步骤类型列表'"  @click="onClickBack" />
          </q-toolbar>
        </div>
        
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">
              <div>步骤类型 - 准备环境</div>
              <q-skeleton v-if="!pipelineName" type="text" animation="fade" style="max-width: 150px;" />
              <q-breadcrumbs v-else class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el :label="`${pipelineName}`"/>
                <q-breadcrumbs-el :label="`第 ${parseInt(stepPosition)+1} 步`" />
              </q-breadcrumbs>
            </div>

            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input outlined bg-color="white" label="名称 *" v-model="step.name" 
                lazy-rules
                :rules="[
                  (val) => (val && val.length > 0) || '请输入名称',
                  (val) => val.length <= 100 || '名称长度不超过100个字符',
                ]"
              />
              <q-card flat>
                <q-tabs
                  align="justify" 
                  v-model="tab" 
                  active-color="primary"
                  narrow-indicator
                >
                  <q-tab name="agent" label="节点容器" />
                  <q-tab name="variable" label="环境变量" />
                  <q-tab name="file" label="文件目录" />
                </q-tabs>
                <q-separator />
                <q-tab-panels v-model="tab">
                  <q-tab-panel name="agent">
                    <EnvAgent />
                  </q-tab-panel>

                  <q-tab-panel name="variable">
                    <EnvVariable />
                  </q-tab-panel>

                  <q-tab-panel name="file">
                    <EnvFile />
                  </q-tab-panel>
                </q-tab-panels>
              </q-card>

              <div class="q-mt-md">
                <q-btn unelevated type="submit" color="primary" :label="isModeEdit ? '保存' : '添加'" />
                <q-btn flat class="q-ml-sm bg-grey-2" label="返回" @click="onClickBack" />
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
.step-type-env{
  .tab-heading{
    line-height: 36px;
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
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, provide, watch } from 'vue'
import EnvAgent from './env/EnvAgent'
import EnvVariable from './env/EnvVariable'
import EnvFile from './env/EnvFile'
import pipelineApi from '@/api/pipeline.api'

export default {
  components: {
    LayoutTwoColumn, EnvAgent, EnvVariable, EnvFile
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, stepPosition, stepType } = route.params
    const isModeEdit = computed(() => !stepType)
    const tab = ref('agent')
    let step = inject('step')
    if(!step.value){
      step = ref({
        name: dayjs().format('准备环境 YYYYMMDDHHmmss'),
        type: 'remote-prepare-env',
        params: {
          'agentId': null,
          'image': null,
          'imageServerAddress': null,
          'imageServerUsername': null,
          'imageServerPassword': null,
          'initScript': null,
          'env': {},
          'injectVariableIntoEnv': true,
          'cacheDirs': null,
          'syncProjectFile': false,
          'syncPipelineFile': true,
        },
      })

      provide('step', step)
    }

    const onClickAddStep = () => {
      pipelineApi.addStep({ 
        pipelineId,
        type: step.value.type,
        name: step.value.name,
        params: step.value.params,
        position: stepPosition,
      }).then(resp => {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
      })
    }

    const onClickSaveStep = () => {
      pipelineApi.updateStep({ 
        pipelineId,
        position: stepPosition,
        step: step.value,
      }).then(resp => {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
      })
    }

    return {
      pipelineName,
      stepPosition,
      isModeEdit,
      tab,
      step,

      onClickBack(){
        if(isModeEdit.value){
          router.push(`/project/${projectId}/pipeline/${pipelineId}/step`)
        }else{
          router.push(`/project/${projectId}/pipeline/${pipelineId}/step/${stepPosition}/add`)
        }
      },

      onSubmit(){
        if(isModeEdit.value){
          onClickSaveStep()
        }else{
          onClickAddStep()
        }
      },

    }
  }
};
</script>