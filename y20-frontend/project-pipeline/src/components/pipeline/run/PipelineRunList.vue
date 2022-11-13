<template>
  <div class="pipeline-run-list">
    <div class="page-heading text-primary q-pb-md">执行历史</div>
    <q-table
      flat
      :rows="pipelineRuns"
      :columns="columns"
      v-model:pagination="pagination"
      :rows-per-page-options="[5, 10, 20, 50]"
      row-key="pipelineRunId"
      :filter="filter"
      :loading="loading"
      @request="onTableSearch"
    >
      <template v-slot:body-cell-name="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickDetailPipelineRun(props.row.pipelineRunId)">
          <span>{{ props.row.name }}</span>
        </q-td>
      </template>
      <template v-slot:body-cell-status="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickDetailPipelineRun(props.row.pipelineRunId)">
          <tempalte v-if="['STARTED', 'RUNNING', 'WAITING'].includes(props.row.status)">
            <q-icon name="update" color="orange" class="q-mr-xs" size="xs" />
            <span class="text-orange">{{props.row.status}}</span>
          </tempalte>
          <tempalte v-else-if="props.row.status == 'PASSED'">
            <q-icon name="check_circle" color="green" class="q-mr-xs" size="xs" />
            <span class="text-green">{{props.row.status}}</span>
          </tempalte>
          <tempalte v-else-if="props.row.status == 'FAILED'">
            <q-icon name="close" color="red" class="q-mr-xs" size="xs" />
            <span class="text-red">{{props.row.status}}</span>
          </tempalte>
          <tempalte v-else>
            <span>{{props.row.status}}</span>
          </tempalte>
        </q-td>
      </template>
      <template v-slot:body-cell-beginTime="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickDetailPipelineRun(props.row.pipelineRunId)">
          <span v-if="props.row.beginTime">
            {{ dayjs(props.row.beginTime).format('YYYY-MM-DD HH:mm:ss') }}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-endTime="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickDetailPipelineRun(props.row.pipelineRunId)">
          <span v-if="props.row.endTime">
            {{ dayjs(props.row.endTime).format('YYYY-MM-DD HH:mm:ss') }}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-elapsedTime="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickDetailPipelineRun(props.row.pipelineRunId)">
          <span v-if="props.row.beginTime">
            {{ dayjs.duration((props.row.endTime || Date.now()) - props.row.beginTime).format('HH:mm:ss') }}
          </span>
        </q-td>
      </template>
      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-btn icon="delete" flat dense color="negative" label="" @click="onClickDeletePipelineRun(props.row.name, props.row.pipelineRunId)" />
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
  { name: 'name', label: '名称', field: 'name', align: 'left', sortable: true },
  { name: 'status', label: '状态', field: 'status', align: 'left', sortable: true },
  { name: 'beginTime', label: '启动时间', field: 'beginTime', align: 'left', sortable: true },
  { name: 'endTime', label: '结束时间', field: 'endTime', align: 'left', sortable: true },
  { name: 'elapsedTime', label: '持续时间', field: '', align: 'left', sortable: false },
  { name: 'action', label: '操作', field: '', align: 'left', sortable: false },
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
      sortBy: 'id',
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const pipelineRuns = ref([])

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
      }
      pipelineApi.listRun({ 
        pipelineId,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        loading.value = false
        pipelineRuns.value = resp.data.page
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
      pipelineRuns,

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

      onClickDetailPipelineRun(pipelineRunId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run/${pipelineRunId}`)
      },

      onClickDeletePipelineRun(pipelineRunName, pipelineRunId){
        $q.dialog({
          title: '删除',
          message: `确定删除执行 ${pipelineRunName} ？`,
          cancel: true,
        }).onOk(data => {
          pipelineApi.deleteRun({ pipelineId, pipelineRunId }).then(resp => {
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