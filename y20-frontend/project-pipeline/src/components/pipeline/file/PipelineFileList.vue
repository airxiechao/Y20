<template>
  <div class="pipeline-file-list">
    <div class="row q-pb-md">
      <div class="col page-heading self-center">流水线文件</div>
      <div class="self-center">
        <q-btn flat icon="upload" label="上传文件" @click="onClickUpload" />
      </div>
    </div>
    <q-card flat>
      <div>
        <FsViewer
          :nodes="pipelineFiles"
            @lazy-load="onLazyLoad"
            @reload="onClickSearch"
            @download="onClickDownload"
            @delete="onClickDelete"
            :loading="loading"
        />
      </div>
        <!-- <q-tree
          :nodes="pipelineFiles"
          node-key="label"
          dark
          default-expand-all
          @lazy-load="onLazyLoad"
        >
          <template v-slot:header-generic="prop">
            <div class="row items-center">
              <q-icon :name="prop.node.icon" class="q-mr-sm" />
              <div class="q-mr-sm">{{ prop.node.label }}</div>
              <div class="q-mr-sm text-grey" v-if="!prop.node.isDir">
                {{formatFileSize(prop.node.size)}}
              </div>
              <div class="q-mr-sm text-grey" v-if="prop.node.lastModified">
                {{dayjs(prop.node.lastModified).format('YYYY-MM-DD HH:mm:ss')}}
              </div>
              <q-btn flat dense icon="link" @click.stop>
                <q-popup-proxy>
                  <div class="q-pa-sm bg-white">
                    {{prop.node.path}}
                    <q-btn flat dense icon="file_copy" size="sm" class="q-ml-sm" @click="onClickCopyPath(prop.node.path)" />
                  </div>
                </q-popup-proxy>
              </q-btn>
              <q-btn v-if="!prop.node.isDir" flat dense icon="get_app" @click.stop="onClickDownload(prop.node.path)" />
              <q-btn flat dense icon="delete" @click.stop="onClickDelete(prop.node.label, prop.node.path)" />
            </div>
          </template>
        </q-tree> -->
    </q-card>
  </div>
</template>

<style lang="scss">
.pipeline-file-list{
  
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
    FsViewer
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const { projectId, pipelineId } = route.params

    const pipelineFiles = ref([])
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
    
    const listPipelineFile = () => {
      loading.value = true
      artifactApi.listPipelineFile({ pipelineId }).then((resp) => {
        pipelineFiles.value = resp.data.map(file => {
          return buildFileNode(file)
        })
      }, (resp) => {
         qUtil.notifyError(resp.message)
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      listPipelineFile()
    })

    return {
      dayjs,

      pipelineFiles,
      loading,

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

      onClickBack(){
        router.push('/workspace/project')
      },

      onClickSearch(){
        listPipelineFile()
      },

      onClickDownload({ path }){
        artifactApi.downloadUserFile({ path })
      },

      onClickUpload(){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/file/upload`)
      },

      onClickDelete({ name, path, done, fail }){

        $q.dialog({
          title: '删除',
          message: `确定删除文件 ${name} ？`,
          cancel: true,
        }).onOk(data => {
          loading.value = true
          artifactApi.removeUserFile({ path }).then(resp => {
            done()
          }, resp => {
             fail()
             qUtil.notifyError(resp.message)
          }).finally(() => {
            loading.value = false
          })
        })
        
      }

    }
  }
};
</script>