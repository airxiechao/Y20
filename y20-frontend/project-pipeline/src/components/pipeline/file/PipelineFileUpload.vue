<template>
  <div class="pipeline-file-upload">
    <div class="q-pb-md">
      <div class="page-heading">上传文件</div>
    </div>
    <div class="q-gutter-md">
      <div>
        <q-input outlined v-model="dir" 
          label="目标目录" 
          hint="相对流水线文件的相对目录"
        />
      </div>
      <div>
        <q-uploader
          flat bordered
          :label="`本地文件 -> 流水线目录：${dir || '.'}`"
          :url="uploadPipelineFileUrl"
          :headers="uploadPipelineFileHeaders"
          :formFields="uploadPipelineFileFields"
          fieldName="file"
          auto-upload
        />
        <div class="q-mt-md"> 
          <q-btn flat class="bg-grey-2" label="返回" :to="`/project/${projectId}/pipeline/${pipelineId}/file`" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.pipeline-file-upload{
  
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

import { ref, inject, computed, onMounted } from 'vue'
import PipelineFileUpload from '@/components/pipeline/file/mixin/PipelineFileUpload.mixin'


export default {
  mixins: [ PipelineFileUpload ],
  setup(){
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, pipelineId } = route.params

    return {
      dayjs,
      projectId,
      projectName,
      pipelineId,
    }
  }
};
</script>