<template>
  <div class="fs-viewer">
    <q-toolbar class="bg-grey-3">
      <q-btn flat round dense icon="arrow_upward" :disable="breadcrumbs.length == 1" @click="onClickUp(1)" />
      <q-toolbar-title>
        <q-breadcrumbs style="font-size: 14px">
          <q-breadcrumbs-el 
            v-for="(label,i) in breadcrumbs" 
            :key="label" 
            class="cursor-pointer"
            :label="label == '/' ? undefined : label" 
            :icon="label == '/' ? 'home' : undefined" 
            @click="onClickUp(breadcrumbs.length - (i+1))"
          />
        </q-breadcrumbs>
      </q-toolbar-title>
      <q-btn flat round dense icon="refresh" @click="onClickRefresh" />
    </q-toolbar>
    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="path"
      :pagination="{
        rowsPerPage: 10,
      }"
      :rows-per-page-options="[5, 10, 20, 50, 0]"
      :loading="loading"
    >
      <template v-slot:body-cell-label="props">
        <q-td :props="props" :class="{
          'cursor-pointer': props.row.isDir,
        }" @click="onClickDir(props.row)">
          <q-icon :name="props.row.icon" class="q-mr-xs vertical-middle" size="xs" />
          <span class="q-mr-sm vertical-middle">
            {{ props.value }}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-size="props">
        <q-td :props="props" :class="{
          'cursor-pointer': props.row.isDir,
        }" @click="onClickDir(props.row)">
          <span v-if="!props.row.isDir">
            {{formatFileSize(props.value)}}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-lastModified="props">
        <q-td :props="props" :class="{
          'cursor-pointer': props.row.isDir,
        }" @click="onClickDir(props.row)">
          <span v-if="props.value">
            {{dayjs(props.value).format('YYYY-MM-DD HH:mm:ss')}}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-btn flat dense icon="link" size="sm" @click.stop>
            <q-popup-proxy>
              <div class="q-pa-sm bg-white">
                <span class="vertical-middle">{{props.row.path}}</span>
                <q-btn flat dense icon="file_copy" size="sm" class="vertical-middle q-ml-xs" @click="onClickCopyPath(props.row.path)" />
              </div>
            </q-popup-proxy>
          </q-btn>
          <q-btn v-if="!props.row.isDir && !downloadDisabled" flat dense icon="get_app" size="sm" @click.stop="onClickDownload(props.row.path)" />
          <q-btn v-if="!deleteDisabled" flat dense size="sm" icon="delete" color="negative" @click.stop="onClickDelete(props.row.label, props.row.path)" />
        </q-td>
      </template>
    </q-table>
  </div>
</template>

<style lang="scss" scoped>
.fs-viewer{
  border: 1px solid #ddd;
}
</style>

<script>
import { copyToClipboard  } from 'quasar'
import { ref, inject, watch, computed } from 'vue'
import dayjs from 'dayjs'

const columns = [
  { name: 'label', align: 'left', label: '名称', field: 'label', sortable: true },
  { name: 'size', align: 'left', label: '大小', field: 'size', sortable: true },
  { name: 'lastModified', align: 'left', label: '修改日期', field: 'lastModified', sortable: true },
  { name: 'action', align: 'right', label: '操作', headerStyle: 'width: 5%' },
]

export default {
  props: {
    nodes: {
      type: Array,
      default: [],
    },
    downloadDisabled: {
      type: Boolean,
      default: false,
    },
    deleteDisabled: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { emit }){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const path = ref(".")
    const breadcrumbs = ref(['/'])

    const getFileNode = (fileNodes, path) => {
      for(let i = 0; i < fileNodes.length; ++i){
        const file = fileNodes[i]
        if(file.path === path){
          return file
        }
        
        if(file.children){
          const childrenFile = getFileNode(file.children, path)
          if(childrenFile){
            return childrenFile
          }
        }
      }

      return null
    }

    const getParentPath = (path) => {
      if(breadcrumbs.value.length == 2){
        return '.'
      }

      const tokens = path.split('/')
      tokens.splice(-1)

      return tokens.join('/')
    }

    const deleteFileNode = (fileNodes, path) => {
      for(let i = 0; i < fileNodes.length; ++i){
        const file = fileNodes[i]
        if(file.path === path){
          fileNodes.splice(i,1)
          return
        }else{
          if(file.children){
            deleteFileNode(file.children, path)
          }
        }
      }
    }

    const rows = computed(() => {
      if(path.value == '.'){
        return props.nodes
      }else{
        const fileNode = getFileNode(props.nodes, path.value)
        if(fileNode){
          return fileNode.children
        }

        return []
      }
    })

    const loading = computed(() => {
      return props.loading
    })

    const downloadDisabled = computed(() => {
      return props.downloadDisabled
    })

    const deleteDisabled = computed(() => {
      return props.deleteDisabled
    })

    return {
      dayjs,

      path,
      breadcrumbs,
      
      columns,
      rows,

      loading,
      downloadDisabled,
      deleteDisabled,

      formatFileSize(size){
        if(!size){
          return '0 B'
        }else if(size < 1024){
          return  `${size} B`
        }else if(size < 1024 * 1024){
          return `${(size/1024).toFixed(2)} KB`
        }else if(size < 1024 * 1024 * 1024){
          return `${(size/1024/1024).toFixed(2)} MB`
        }else{
          return `${(size/1024/1024/1024).toFixed(2)} GB`
        }
      },

      onClickUp(level){
        for(let i = 0; i < level; ++i){
          if(breadcrumbs.value.length == 1){
            return
          }
          const parentPath = getParentPath(path.value)
          path.value = parentPath
          breadcrumbs.value.splice(-1)
        }
      },

      onClickRefresh(){
        if(breadcrumbs.value.length == 1){
          emit('reload')
          return
        }

        const node = getFileNode(props.nodes, path.value)
        emit('lazy-load', { node, done: (newNodes) => {
          const fileNode = getFileNode(props.nodes, node.path)
          if(fileNode){
            fileNode.children = newNodes
          }
        }, fail: () => {
          
        }})
      },

      onClickDir(node){
        if(!node.isDir){
          return
        }

        emit('lazy-load', { node, done: (newNodes) => {
          breadcrumbs.value.push(node.label)
          path.value = node.path

          const fileNode = getFileNode(props.nodes, node.path)
          if(fileNode){
            fileNode.children = newNodes
          }
        }, fail: () => {
          
        }})
      },

      onClickCopyPath(path){
        copyToClipboard(path).then(() => {
            qUtil.notifySuccess('已复制到粘贴板')
          }).catch(() => {
            qUtil.notifyError('复制到粘贴板发生错误')
          })
      },

      onClickDownload(path){
        emit('download', { path })
      },

      onClickDelete(name, path){
        emit('delete', { name, path, done: () => {
          deleteFileNode(props.nodes, path)
        }, fail: () => {

        }})
      },
    }
  },
}
</script>