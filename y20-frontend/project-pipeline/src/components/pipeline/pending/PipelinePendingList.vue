<template>
  <div class="pipeline-pending-list">
    <div class="page-heading text-primary q-pb-md">等待队列</div>
    <q-table
      flat
      :rows="pipelinePendings"
      :columns="columns"
      v-model:pagination="pagination"
      :rows-per-page-options="[5, 10, 20, 50]"
      row-key="pipelinePendingId"
      :filter="filter"
      :loading="loading"
      @request="onTableSearch"
    >
      <template v-slot:body-cell-name="props">
        <q-td :props="props">
          <span class="cursor-pointer" @click="onClickDetailPipelinePending(props.row.pipelinePendingId)">
            <span>{{ props.row.name }}</span>
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-createTime="props">
        <q-td :props="props">
          <span v-if="props.row.createTime">
            {{ dayjs(props.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-btn icon="delete" flat dense color="negative" label="" @click="onClickDeletePipelinePending(props.row.name, props.row.pipelinePendingId)" />
        </q-td>
      </template>
    </q-table>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { ref, inject, onMounted, onUnmounted } from 'vue'
import pipelineApi from '@/api/pipeline.api'

const columns = [
  { name: 'name', label: '执行名称', field: 'name', align: 'left', sortable: true },  
  { name: 'createReason', label: '等待原因', field: 'createReason', align: 'left', sortable: false },
  { name: 'createTime', label: '创建时间', field: 'createTime', align: 'left', sortable: true },
  { name: 'action', label: '操作', field: '', align: 'left', sortable: false, headerStyle: "width: 5%;" },
]

export default {
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const { projectId, pipelineId } = route.params

    const loading = ref(false)
    const pagination = ref({
      sortBy: 'createTime',
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const pipelinePendings = ref([])

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
      }
      pipelineApi.listPending({ 
        pipelineId,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        loading.value = false
        pipelinePendings.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, reps => {
        loading.value = false
      })
    }

    let timer = null
    const startTimer = () => {
      const delay = 10*1000
      if(!timer){
        timer = setInterval(() => {
          search(true)
        }, delay)
      }
    }

    const stopTimer = () => {
      if(timer){
        clearInterval(timer)
      }
    }

    onMounted(() => {
      search()
      startTimer()
    })

    onUnmounted(() => {
      stopTimer()
    })

    return {
      dayjs,
      
      projectId,
      pipelineId,
      pipelinePendings,

      columns,
      filter: ref(''),
      pagination,
      loading,

      onClickSearch(){
        pagination.value.page = 1
        search()
      },

      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        search()
      },

      onClickDetailPipelinePending(pipelinePendingId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/pending/${pipelinePendingId}`)
      },

      onClickDeletePipelinePending(pipelinePendingName, pipelinePendingId){
        $q.dialog({
          title: '删除',
          message: `确定删除等待 ${pipelinePendingName} ？`,
          cancel: true,
        }).onOk(data => {
          pipelineApi.deletePending({ pipelineId, pipelinePendingId }).then(resp => {
            search()
          }, resp => {
            qUtil.notifyError(resp.message || '删除发生错误')
          })
        })
      },
    }
  }
}
</script>