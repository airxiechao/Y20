<template>
  <LayoutTwoColumn class="pipeline-run-detial-file">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="返回" @click="onClickBack" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div class="q-pb-md page-heading">
              <div>上传的文件</div>
              <q-breadcrumbs class="text-subtitle2 text-grey" active-color="grey">
                <q-breadcrumbs-el :label="`${pipelineRun.name}`" />
              </q-breadcrumbs>
            </div>
            <div>
              <FsViewer
                :nodes="pipelineRunFiles"
                  @lazy-load="onLazyLoad"
                  @reload="listPipelineRunFile"
                  @download="onClickDownload"
                  :deleteDisabled="true"
                  :loading="loading"
              />
            </div>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <q-list class="text-primary">
      </q-list>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.pipeline-run-detial-file{
  
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
import { LayoutTwoColumn, FsViewer } from 'common'
import { ref, inject, computed, onMounted } from 'vue'

import pipelineApi from '@/api/pipeline.api'
import artifactApi from '@/api/artifact.api'

export default {
  components: {
    LayoutTwoColumn, FsViewer,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, pipelineRunId } = route.params
    const pipelineRun = ref({})

    const pipelineRunFiles = ref([])
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
    
    const listPipelineRunFile = () => {
      loading.value = true
      artifactApi.listPipelineRunFile({ pipelineId, pipelineRunId }).then((resp) => {
        pipelineRunFiles.value = resp.data.map(file => {
          return buildFileNode(file)
        })
      }, (resp) => {
         qUtil.notifyError(resp.message)
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      pipelineApi.getRun({ pipelineId, pipelineRunId}).then(resp => {
        pipelineRun.value = resp.data.pipelineRun 
      })

      listPipelineRunFile()
    })

    return {
      dayjs,

      projectId,
      pipelineId,
      pipelineName,
      pipelineRunId,
      pipelineRun,
      pipelineRunFiles,

      loading,

      listPipelineRunFile,

      onClickBack(){
        router.back()
      },

      onLazyLoad({ node, key, done, fail }){
        loading.value = true
        artifactApi.listUserFile({ path: node.path }).then(
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
        artifactApi.downloadUserFile({ path })
      },

    }
  }
};
</script>