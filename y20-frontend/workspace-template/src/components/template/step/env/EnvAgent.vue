<template>
  <div class="env-agent">
    <div class="q-gutter-md">
      <div class="tab-heading text-primary q-my-md">节点</div>
      <q-input
        readonly
        outlined
        v-model="step.params['agentId']"
        label="节点 *"
        :rules="[val => !!val]"
      />
      <div class="tab-heading text-primary q-my-md">Docker</div>
      <q-input readonly outlined v-model="step.params['image']" label="镜像" />
      <q-input readonly outlined v-model="step.params['imageServerAddress']" label="仓库地址" />
      <q-input readonly outlined v-model="step.params['imageServerUsername']" label="仓库用户名" />
      <q-input readonly outlined type="password" v-model="step.params['imageServerPassword']" label="仓库密码" />
    </div>
  </div>
</template>

<script>
import { ref, inject, onMounted } from 'vue'

export default {
  components: {
    
  },
  setup(){
    const step = inject('step')

    return {
      step,
    }
  },
}
</script>