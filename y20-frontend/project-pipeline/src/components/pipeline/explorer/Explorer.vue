<template>
  <div class="explorer q-pt-sm bg-grey-10 text-white">
    <div class="tree q-px-sm q-mb-sm q-mr-sm">
      <q-tree
        :nodes="explorerRunfiles"
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
            <q-btn v-if="!prop.node.isDir" flat dense icon="get_app" @click.stop="onClickDownload(prop.node.path)" />
            <q-btn v-if="prop.node.isDir" flat dense icon="drive_folder_upload" @click.stop="onClickTargetDir(prop.node.path)">
              <q-tooltip>上传到</q-tooltip>
            </q-btn>
          </div>
        </template>
      </q-tree>
    </div>
    <div class="action relative-position full-width">
      <div style="position: absolute; width: 100%; top: -25px;">
        <template v-for="(item, path) in progresses" :key="path">
          <q-linear-progress v-if="item.visible" stripe size="25px" :value="item.progress" color="accent">
            <div class="absolute-full flex flex-center">
              <q-badge color="white" text-color="accent" :label="item.label" />
              <q-btn class="q-ml-xs" size="xs" dense flat color="white" icon="cancel" @click="onClickStopProgress(item.direction, path)"/>
            </div>
          </q-linear-progress>
        </template>
      </div>
      <q-file
        class="q-pl-md"
        v-model="uploadFile"
        :label="`选择文件（上传到 ${targetUploadDir || '.'}）：`"
        square
        dense
        dark
        borderless
      >
        <template v-slot:append>
          <q-btn flat label="上传" :loading="uploadLoading" @click="onClickUpload" />
        </template>
        <template v-slot:after>
          <q-btn flat label="刷新" @click="onClickRefresh" />
        </template>
      </q-file>
    </div>
  </div>
</template>

<style lang="scss">
.explorer{
  height: 416px;

  .tree{
    height: 360px; 
    overflow-y: auto;

    &::-webkit-scrollbar {
      background-color: #212121;
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      background: #777;
    }
  }

  .action{
    border-top: 1px solid #777;
  }
}
</style>

<script>
import dayjs from 'dayjs'
import { ref, inject, computed, onMounted, onBeforeUnmount } from 'vue'

import pipelineApi from '@/api/pipeline.api'

export default {
  props: ['explorerRunInstanceUuid', 'socketUrl'],
  setup(props){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const store = inject('useStore')()
    const pipelineName = inject('pipelineName')
    const { projectId, pipelineId, pipelineRunId } = route.params
    const explorerRunInstanceUuid = props.explorerRunInstanceUuid

    const explorerRunfiles = ref([])
    const uploadFile = ref(null)
    const targetUploadDir = ref('')
    const uploadLoading = ref(false)

    let socket = null
    const progresses = ref({})
    const cancelTokens = {}

    const addProgress = ({path, direction, speed, progress, visible}) => {
      let label
      switch (direction) {
        case 'SERVER':
          label = '上传到服务器'
          break;
        case 'UPLOAD':
          label = '推送到节点'
          break;
        default:
          break;
      }
      if($q.screen.lt.md){
        label = `${label}：${(speed/1024).toFixed(2)}KB/s，${(progress*100).toFixed(2)}%`
      }else{
        label = `${path} ${label}：${(speed/1024).toFixed(2)}KB/s，${(progress*100).toFixed(2)}%`
      }
      
      let item = progresses.value[path]
      if(item){
        if(visible === undefined){
          visible = item.visible
        }
      }

      progresses.value[path] = {
        direction,
        label,
        progress,
        visible,
      }
      
      if(direction == 'UPLOAD' && progress == 1){
        removeProgress(path)
      }
    }

    const removeProgress = (path) => {
      progresses.value[path].visible = false
    }

    const initSocket = () => {
      socket = new WebSocket(props.socketUrl)

      socket.addEventListener('open', function (event) {
        console.log('explorer ws open')
      })

      socket.addEventListener('message', function (event) {
        let data = JSON.parse(event.data)
        let type = data.type
        switch(type){
          case 'y20_pipeline_run_explorer_transfer_progress':
            const { path, direction, total, speed, progress } = data
            addProgress({
              path,
              direction,
              speed,
              progress,
            })
            break;
        }
      })

      socket.addEventListener('close', function (event) {
        console.log('explorer ws close')
      })
    }

    const buildFileNode = file => {
      return {
        label: file.name,
        icon: file.dir ? 'folder' : 'insert_drive_file',
        header: 'generic',
        path: file.path,
        isDir: file.dir,
        lazy: file.dir,
        lastModified: file.lastModified,
        size: file.size,
      }
    }

    const listFileExplorerRun = () => {
      pipelineApi.listFileExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid, path: '/' }).then((resp) => {
        explorerRunfiles.value = resp.data.map(file => {
          return buildFileNode(file)
        })
      }, (resp) => {
         qUtil.notifyError(resp.message)
      })
    }

    onMounted(() => {
      initSocket()
      listFileExplorerRun()
    })

    onBeforeUnmount(() => {
      if(socket){
        socket.close()
      }
    })
    
    return {
      dayjs,

      explorerRunfiles,
      uploadFile,
      targetUploadDir,
      uploadLoading,

      progresses,
      cancelTokens,

      formatFileSize(size){
        if(!size){
          return '0B'
        }else if(size < 1024){
          return  `${size}B`
        }else if(size < 1024 * 1024){
          return `${(size/1024).toFixed(2)}K`
        }else if(size < 1024 * 1024 * 1024){
          return `${(size/1024/1024).toFixed(2)}M`
        }else{
          return `${(size/1024/1024/1024).toFixed(2)}G`
        }
      },

      onLazyLoad({ node, key, done, fail }){
        pipelineApi.listFileExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid, path: node.path }).then(
          (resp) => {
            done(resp.data.map(file => {
              return buildFileNode(file)
            }))
          }, (resp) => {
            qUtil.notifyError(resp.message)
            fail()
          })
      },

      onClickRefresh(){
        listFileExplorerRun()
        uploadFile.value = null
        targetUploadDir.value = ''
      },

      onClickDownload(path){
        pipelineApi.downloadFileExplorerRun({ projectId, pipelineId, pipelineRunId, explorerRunInstanceUuid, path })
      },

      onClickTargetDir(path){
        targetUploadDir.value = path
      },

      onClickUpload(){
        if(!uploadFile.value){
          qUtil.notifyError("没有选择文件")
          return
        }

        uploadLoading.value = true
        const dir = targetUploadDir.value
        const file = uploadFile.value
        pipelineApi.uploadFileExplorerRun({ 
          projectId, 
          pipelineId, 
          pipelineRunId, 
          explorerRunInstanceUuid, 
          dir,
          file,
          progressCallback(path, speed, progress){
            addProgress({
              path,
              direction: 'SERVER',
              speed,
              progress,
              visible: true,
            })
          },
          fCancelToken(path, c){
            cancelTokens[path] = c
          },
        }).then(resp => {
          listFileExplorerRun()
        }, resp => {
          //qUtil.notifyError(resp.message)
        }).finally(() => {
          uploadLoading.value = false
          const path = `${dir?dir+'/':''}${file.name}`
          removeProgress(path)
        })
      },

      onClickStopProgress(direction, path){
        switch(direction){
          case 'SERVER':
            let cancel = cancelTokens[path]
            if(cancel){
              cancel()
            }
          case 'UPLOAD':
            pipelineApi.stopUploadFileExplorerRun({ 
              projectId, 
              pipelineId, 
              pipelineRunId, 
              explorerRunInstanceUuid, 
              path 
            }).then(resp => {
              removeProgress(path)
            }, resp => {
              qUtil.notifyError("停止上传发生错误")
            })

            break;
        }
      }
    }
  },
}
</script>