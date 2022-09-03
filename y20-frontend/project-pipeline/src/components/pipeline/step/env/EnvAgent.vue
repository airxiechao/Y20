<template>
  <div class="env-agent">
    <div class="q-gutter-md">
      <div class="tab-heading text-primary q-my-md">节点</div>
      <SelectAgent
        v-model="step.params['agentId']"
        label="节点 *"
        :rules="[
          val => !!val || '请选择节点。输入 {{变量名}} 回车，可以引用变量'
        ]"
      />
      <div class="tab-heading text-primary q-my-md">Docker</div>
      <q-input outlined v-model="step.params['image']" label="镜像" />
      <q-input outlined v-model="step.params['imageServerAddress']" label="仓库地址" />
      <q-input outlined v-model="step.params['imageServerUsername']" label="仓库用户名" />
      <q-input outlined type="password" autocomplete="new-password" v-model="step.params['imageServerPassword']" label="仓库密码" />
    </div>
  </div>
</template>

<script>
import { ref, inject, onMounted } from 'vue'
import SelectAgent from '@/components/pipeline/common/SelectAgent'

export default {
  components: {
    SelectAgent,
  },
  setup(){
    const step = inject('step')

    return {
      step,
    }
  },
}
</script>