<template>
  <LayoutTwoColumn class="upload-project-file">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="文件" :to="`/project/${projectId}/file`" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="q-pa-md">
            <div class="q-pb-md">上传文件</div>
            <div class="q-gutter-md">
              <div>
                <q-input outlined v-model="dir" 
                  label="目标目录" 
                  hint="相对项目文件的相对目录"
                />
              </div>
              <div>
                <q-uploader
                  :label="`本地文件 -> 项目目录：${dir||'.'}`"
                  :url="uploadProjectFileUrl"
                  :headers="uploadProjectFileHeaders"
                  :formFields="uploadProjectFileFields"
                  fieldName="file"
                />
              </div>
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.upload-project-file{
  
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
import { ref, inject, computed, onMounted } from 'vue'
import projectFileUploadMixin from '@/components/file/mixin/projectFileUpload.mixin'

export default {
  mixins: [ projectFileUploadMixin ],
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId } = route.params

    return {
      dayjs,
      projectId,
      projectName,

    }
  }
};
</script>