<template>
  <div class="env-file">
    <div class="q-gutter-md">
      <div class="tab-heading text-primary q-my-md">本地缓存</div>
      <div>
        <q-input readonly outlined v-model="step.params['cacheDirs']" label="目录名" hint="缓存本地工作空间的目录，流水线运行环境会复用这些目录，结束后不会删除；多个目录以英文逗号（,）隔开。通过节点的“清理”操作可以删除缓存" />
      </div>
      <div class="tab-heading text-primary q-my-md">文件同步</div>
      <div><q-checkbox label="同步项目文件到工作目录" :model-value="step.params['syncProjectFile']" /></div>
      <div><q-checkbox label="同步流水线文件到工作目录" :model-value="step.params['syncPipelineFile']" /></div>
    </div>
  </div>
</template>


<script>
import { ref, inject, onMounted } from 'vue'

export default {
  setup(){
    const step = inject('step')

    return {
      step,
    }
  },
}
</script>