<template>
  <LayoutTwoColumn class="token-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-input dense outlined class="full-width" bg-color="white" v-model="name" placeholder="搜索令牌" @change="onClickSearch">
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
              <template v-slot:after>
                <q-btn unelevated icon="add" color="primary" label="创建新令牌" @click="onClickCreateToken" />
              </template>
            </q-input>
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <div class="page-heading">令牌</div>
          <q-table
            flat
            class="token-list-table q-mt-sm"
            :rows="tokens"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[5, 10, 20, 50]"
            row-key="id"
            :filter="filter"
            @request="onTableSearch"
            :loading="loading"
          >
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
                  <span class="vertical-middle">{{ dayjs(props.value).format('YYYY-MM-DD HH:mm:ss') }}</span>
                  <q-badge v-if="props.value <= new Date().getTime()" class="vertical-middle q-ml-xs" color="warning">已过期</q-badge>
                  <q-badge v-else-if="props.value - new Date().getTime() < 30*24*60*60*1000" class="vertical-middle q-ml-xs" color="warning">即将过期</q-badge>
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-flagCurrentSession="props">
              <q-td :props="props">
                <div>
                  <q-icon v-if="props.value" size="20px" color="green" name="check" />
                </div>
              </q-td>
            </template>
            <template v-slot:body-cell-action="props">
              <q-td :props="props">
                <q-btn v-if="!props.row.flagCurrentSession" color="negative" flat dense icon="delete" @click="onClickRevokeToken(props.row.id, props.row.name)">
                  <q-tooltip>撤销</q-tooltip>
                </q-btn>
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
.token-list{
  &-table{
    
  }
}
</style>

<script>
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
dayjs.extend(duration)

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import authApi from '@/api/auth.api'

const columns = [
  { name: 'name', label: '名称', field: 'name', sortable: true },
  { name: 'scope', label: '使用范围', field: 'scope', sortable: true },
  { name: 'beginTime', label: '开始时间', field: 'beginTime', sortable: true },
  { name: 'endTime', label: '过期时间', field: 'endTime', sortable: true },
  { name: 'action', label: '操作' },
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

    const name = ref('')
    const tokens = ref([])
    const loading = ref(false)

    const pagination = ref({
      sortBy: 'beginTime',
      descending: true,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0
    })

    const search = () => {
      loading.value = true
      authApi.listNotUserAccessToken({ 
        name: name.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        loading.value = false
        tokens.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        loading.value = false
      })
    }

    onMounted(() => {
      search()
    })

    return {
      dayjs,

      name,
      tokens,

      columns,
      filter: ref(''),
      pagination,
      loading,

      onClickSearch(){
        pagination.value.page = 1
        search()
      },

      onClickCreateToken(){
        router.push('/user/token/create')
      },

      onTableSearch(props) {
        const { page, rowsPerPage, sortBy, descending } = props.pagination
        const filter = props.filter

        pagination.value = props.pagination
        search()
      },

      onClickRevokeToken(accessTokenId, accessTokenName){
        $q.dialog({
          title: '撤销',
          message: `确定撤销令牌 ${accessTokenName} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          authApi.deleteAccessToken({
            accessTokenId
          }).then(resp => {
            search()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },
    }
  },
}
</script>