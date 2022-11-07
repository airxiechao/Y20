<template>
  <LayoutTwoColumn class="quota-detail">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <div class="row full-width">
              <div class="col page-heading self-center">配额</div>
              <div class="self-center">
                <q-btn unelevated class="q-ml-md" icon="add" color="primary" label="购买配额" to="/user/billing/quota/add" />
              </div>
            </div>
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <div class="q-pb-md">
            <q-chip square>
              <q-avatar icon="data_usage" color="green" text-color="white" />
              <span>免费额度：</span>
              <q-skeleton v-if="loadingFree" type="text" width="100px" />
              <span v-else>{{freeQuotaNumAgent}}个节点，{{freeQuotaNumPipelineRun}}次流水线运行/月</span>
            </q-chip>
          </div>
          <q-card flat class="quota-detail-card">
            <q-list padding dense>
              <q-item-label header>节点接入数</q-item-label>
              
              <q-item>
                <q-item-section>
                  <q-item-label>当前配额</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-item-label class="text-dark">
                    <q-skeleton v-if="loadingCurrent" type="text" width="20px" />
                    <span v-else>{{maxAgent}}</span>
                  </q-item-label>
                </q-item-section>
              </q-item>

              <q-item>
                <q-item-section>
                  <q-item-label>已接入</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-skeleton v-if="loadingCurrent" type="text" width="20px" />
                  <span v-else>
                    {{numAgent}}
                    <q-icon
                      v-if="numAgent > maxAgent"
                      name="warning"
                      color="warning"
                      size="14px"
                      class="q-ml-xs"
                    />
                  </span>
                </q-item-section>
              </q-item>

              <q-separator spaced />

              <q-item-label header>流水线运行次数</q-item-label>

              <q-item>
                <q-item-section>
                  <q-item-label>当前配额</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-item-label class="text-dark">
                    <q-skeleton v-if="loadingCurrent" type="text" width="20px" />
                    <span v-else>{{maxPipelineRun}}</span>
                  </q-item-label>
                </q-item-section>
              </q-item>

              <q-item>
                <q-item-section>
                  <q-item-label lines="1">已运行</q-item-label>
                  <q-item-label v-if="quotaBeginTime" caption lines="1">自 {{ dayjs(quotaBeginTime).format('YYYY-MM-DD HH:mm:ss') }}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <q-skeleton v-if="loadingCurrent" type="text" width="20px" />
                  <span v-else>
                    {{numPipelineRun}}
                    <q-icon
                      v-if="numPipelineRun >= maxPipelineRun"
                      name="warning"
                      color="warning"
                      size="14px"
                      class="q-ml-xs"
                    />
                  </span>
                </q-item-section>
              </q-item>
              
            </q-list>
          </q-card>

          <div class="page-heading q-mt-md">购买记录</div>
          <q-table
            flat
            class="quota-table q-mt-sm"
            :rows="quotas"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="id"
            :filter="filter"
            @request="onTableSearch"
            :loading="loading"
          >
          <template v-slot:body-cell-maxAgent="props">
              <q-td :props="props">
                <div v-if="props.value">
                  +{{ props.value }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-maxPipelineRun="props">
              <q-td :props="props">
                <div v-if="props.value">
                  +{{ props.value }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-beginTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-endTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-createTime="props">
              <q-td :props="props">
                <div>
                  {{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </q-td>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss" scoped>
.quota-detail{
  &-card{
  }
}
</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'

import quotaApi from '@/api/quota.api'

const columns = [
  { name: 'maxAgent', label: '节点数', field: 'maxAgent', sortable: false },
  { name: 'maxPipelineRun', label: '流水线运行数', field: 'maxPipelineRun', sortable: false },
  { name: 'beginTime', label: '开始时间', field: 'beginTime', sortable: true },
  { name: 'endTime', label: '结束时间', field: 'endTime', sortable: true },
  { name: 'createTime', label: '购买时间', field: 'createTime', sortable: true },
]


export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const store = inject('useStore')()
    const router = inject('useRouter')()

    const freeQuotaNumAgent = ref(null)
    const freeQuotaNumPipelineRun = ref(null)
    const numAgent = ref(null)
    const numPipelineRun = ref(null)
    const maxAgent = ref(null)
    const maxPipelineRun = ref(null)
    const quotaBeginTime = ref(null)
    const quotaEndTime = ref(null)

    const quotas = ref([])
    const loading = ref(false)
    const loadingCurrent = ref(false)
    const loadingFree = ref(false)

    const pagination = ref({
      sortBy: 'createTime',
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const listQuota = () => {
      loading.value = true
      quotaApi.list({ 
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        loading.value = false
        quotas.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError(resp.message || '查询购买记录发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    const getFree = () => {
      loadingFree.value = true
      quotaApi.getFreeQuota().then(resp => {
        const data = resp.data
        freeQuotaNumAgent.value = data.freeQuotaNumAgent
        freeQuotaNumPipelineRun.value = data.freeQuotaNumPipelineRun

        loadingFree.value = false
      })
    }

    const getCurrent = () => {
      loadingCurrent.value = true
      quotaApi.getCurrentQuota().then(resp => {
        const data = resp.data
        quotaBeginTime.value = data.beginTime
        maxAgent.value = data.maxAgent
        maxPipelineRun.value = data.maxPipelineRun

        quotaApi.getQuotaUsage().then(resp => {
          const data = resp.data
          numAgent.value = data.numAgent
          numPipelineRun.value = data.numPipelineRun

          loadingCurrent.value = false
        })
      })
    }

    onMounted(() => {
      getFree()
      getCurrent()
      listQuota()
    })

    return {
      dayjs,

      freeQuotaNumAgent,
      freeQuotaNumPipelineRun,

      numAgent,
      numPipelineRun,
      maxAgent,
      maxPipelineRun,
      quotaBeginTime,
      quotaEndTime,

      quotas,

      columns,
      filter: ref(''),
      pagination,
      loading,
      loadingCurrent,
      loadingFree,

      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        listQuota()
      },
    }
  },
}
</script>