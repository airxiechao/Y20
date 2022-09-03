<template>
  <LayoutTwoColumn class="list-project-file">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="项目" :to="`/workspace/project`" />
            <q-toolbar-title></q-toolbar-title>
          <q-btn unelevated color="primary" label="上传文件" icon="upload" @click="onClickUpload" />
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <q-card class="list-project-file-card q-pa-md">
            <div class="page-heading q-pb-md">项目全局文件</div>
            <div>
              <FsViewer
                :nodes="projectFiles"
                 @lazy-load="onLazyLoad"
                 @reload="onClickSearch"
                 @download="onClickDownload"
                 @delete="onClickDelete"
                 :loading="loading"
              />
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
.list-project-file{
  &-card{
    padding-top: 12px;
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

import { LayoutTwoColumn, FsViewer } from 'common'
import { ref, inject, computed, onMounted } from 'vue'

import artifactApi from '@/api/artifact.api'

export default {
  components: {
    LayoutTwoColumn,
    FsViewer,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId } = route.params

    const projectFiles = ref([])
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
    
    const listProjectFile = () => {
      loading.value = true
      artifactApi.listProjectFile().then((resp) => {
        projectFiles.value = resp.data.map(file => {
          return buildFileNode(file)
        })
      }, (resp) => {
         qUtil.notifyError(resp.message)
      }).finally(() => {
        loading.value = false
      })
    }

    onMounted(() => {
      listProjectFile()
    })

    return {
      dayjs,
      projectId,
      projectName,

      projectFiles,
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

      onClickSearch(){
        listProjectFile()
      },

      onClickDownload({ path }){
        artifactApi.downloadUserFile({ path })
      },

      onClickUpload(){
        router.push(`/project/${projectId}/file/upload`)
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