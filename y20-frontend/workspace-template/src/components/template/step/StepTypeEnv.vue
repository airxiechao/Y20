<template>
  <div class="step-type-env">
    <div class="row q-pb-md page-heading">
      <div class="col">
        <div>步骤类型 - 准备环境</div>
        <q-breadcrumbs class="text-subtitle2 text-grey" active-color="grey">
          <q-breadcrumbs-el :label="`第 ${parseInt(stepPosition)+1} 步`" />
        </q-breadcrumbs>
      </div>
    </div>
    <q-form
      class="q-gutter-md"
    >
      <q-input readonly outlined label="名称 *" v-model="step.name" :rules="[val => !!val]" />
      <q-card flat>
        <q-tabs narrow-indicator align="justify" v-model="tab" active-color="primary">
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
      
      <div class="q-pt-sm">
        <q-btn flat class="bg-grey-2" label="返回" @click="onClickBack" />
      </div>
    </q-form>
  </div>
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

import { ref, inject, computed, provide, watch } from 'vue'
import EnvAgent from './env/EnvAgent'
import EnvVariable from './env/EnvVariable'
import EnvFile from './env/EnvFile'

export default {
  components: {
    EnvAgent, EnvVariable, EnvFile
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const { templateId, stepPosition } = route.params
    
    const tab = ref('agent')
    let step = inject('step')

    return {
      tab,
      step,
      stepPosition,

      onClickBack(){
        router.push(`/workspace/template/${templateId}/step`)
      },


    }
  }
};
</script>