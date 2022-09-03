<template>
  <div class="template-file-list">
    <div class="page-heading text-primary q-pb-md">流水线文件</div>
    <q-card flat>
      <div>
        <FsViewer
          :nodes="templateFiles"
          @lazy-load="onLazyLoad"
          @reload="listTemplateFile"
          @download="onClickDownload"
          :deleteDisabled="true"
          :loading="loading"
        />
      </div>
    </q-card>
  </div>
</template>

<style lang="scss">
.template-file-list{
  
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

import { FsViewer } from 'common'
import { ref, inject, computed, onMounted } from 'vue'

import artifactApi from '@/api/artifact.api'

export default {
  components: {
    FsViewer,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const { templateId } = route.params

    const templateFiles = ref([])
    const loading = ref(false)

    const buildFileNode = file => {
      return {
        label: file.name,
        icon: file.dir ? 'folder' : 'insert_drive_file',
        header: 'generic',
        isDir: file.dir,
        path: file.path,
        lazy: file.dir,
        lastModified: file.lastModified,
        size: file.size,
      }
    }
    
    const listTemplateFile = () => {
      loading.value = true
      artifactApi.listTemplateFile({ templateId }).then((resp) => {
        templateFiles.value = resp.data.map(file => {
          return buildFileNode(file)
        })
      }, (resp) => {
         qUtil.notifyError(resp.message)
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      listTemplateFile()
    })

    return {
      dayjs,

      templateFiles,
      loading,

      listTemplateFile,

      onLazyLoad({ node, key, done, fail }){
        loading.value = true
        artifactApi.listTemplateFile({ 
          templateId,
          path: node.path,
        }).then(
          (resp) => {
            done(resp.data.map(file => {
              return buildFileNode(file)
            }))
          }, (resp) => {
            qUtil.notifyError(resp.message)
            fail()
          }).finally(() => {
            loading.value = false
          })
      },

      onClickDownload({ path }){
        artifactApi.downloadTemplateFile({ templateId, path })
      },

    }
  }
};
</script>