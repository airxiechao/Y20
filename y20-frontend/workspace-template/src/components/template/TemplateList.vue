<template>
  <LayoutTwoColumn class="template-list">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-input dense outlined class="full-width" bg-color="white" v-model="name" placeholder="搜索应用" @change="onClickSearch">
              <template v-slot:append>
                <q-icon name="search" class="cursor-pointer" @click="onClickSearch"/>
              </template>
            </q-input>
          </q-toolbar>
        </div>
        <div class="q-pa-md page-content">
          <div class="page-heading q-px-sm">
            <div class="row">
              <div class="self-center">应用市场</div>
              <q-space />
              <div class="self-center">
                <q-toggle class="text-subtitle2" size="sm" v-model="onlyMy" label="我发布的" />
              </div>
            </div>
          </div>
          <q-table
            grid
            :hide-bottom="loading"
            :rows="templates"
            :columns="columns"
            v-model:pagination="pagination"
            :rows-per-page-options="[8, 16, 32]"
            row-key="templateId"
            :filter="filter"
            @request="onTableSearch"
            :loading="loading"
          >
            <template v-slot:loading>
              <div class="row">
                <div class="q-pa-sm col-12 col-md-6 col-lg-3" v-for="i in [1,2,3,4]" :key="i">
                  <q-card class="template-card">
                    <q-list class="q-pa-sm">
                      <q-item>
                        <q-item-section>
                          <q-item-label>
                            <q-skeleton type="text" animation="fade" />
                          </q-item-label>
                          <q-item-label caption>
                            <q-skeleton type="text" animation="fade" />
                          </q-item-label>
                        </q-item-section>
                        <q-item-section side>
                          <q-skeleton type="circle" animation="fade" size="24px" />
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-card>
                </div>
              </div>
            </template>
            <template v-slot:item="props">
              <div class="q-pa-sm col-12 col-md-6 col-lg-3">
                <q-card class="template-card bordered full-height">
                  <q-list class="q-pa-sm">
                    <q-item>
                      <q-item-section>
                        <q-item-label class="cursor-pointer" @click="onClickTemplate(props.row.templateId)">
                          <span class="title">
                            {{props.row.name}}
                          </span>
                        </q-item-label>
                        <q-item-label lines="1" style="word-break: break-all;" caption>
                          <span class="cursor-pointer" @click="onClickTemplateUsername(props.row.username)">{{props.row.username}}</span>
                        </q-item-label>
                        <!-- <q-tooltip anchor="top left" self="bottom left" :offset="[0, 8]">{{ props.row.name }}</q-tooltip> -->
                      </q-item-section>
                      <q-item-section side>
                        <q-btn flat dense round icon="more_vert">
                          <q-menu>
                            <q-list style="min-width: 100px">
                              <q-item clickable v-close-popup @click="onClickApplyTemplate(props.row.templateId)">
                                <q-item-section>创建流水线</q-item-section>
                              </q-item>
                              <template v-if="onlyMy">
                                <q-separator />
                                <q-item clickable v-close-popup @click="onClickDeleteTemplate(props.row.name, props.row.templateId)">
                                  <q-item-section class="text-negative">删除</q-item-section>
                                </q-item>
                              </template>
                            </q-list>
                          </q-menu>
                        </q-btn>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </q-card>
              </div>
            </template>
          </q-table>
        </div>
      </div>
    </template>
    <template v-slot:right>
      <div>
        <div class="text-grey q-mb-md">需要帮助</div>
        <q-list>
          <q-item clickable tag="a" href="/docs/guide/pipeline-template.html" target="_blank">
            <q-item-section avatar>
              <q-icon name="link" />
            </q-item-section>
            <q-item-section>如何发布流水线？</q-item-section>
            <q-item-section side>
              <q-icon name="chevron_right" />
            </q-item-section>
          </q-item>

        </q-list>
      </div>
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.template-list{
  .template-card{

    &.bordered{
      border-left: 2px solid var(--q-primary);
    }

    .title{
      font-size: 16px;
      word-break: break-all;
    }
    
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

import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, watch, onMounted } from 'vue'
import templateApi from '@/api/template.api'
import { PAGE_NAME } from '@/page.config'

const columns = [
  // { name: 'name', label: '名称', field: 'name', sortable: true},
  // { name: 'shortDescription', label: '简介', field: 'shortDescription'},
  // { name: 'username', label: '发布者', field: 'username' },
  { name: 'lastUpdateTime', label: '更新时间', field: 'lastUpdateTime', sortable: true },
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const name = ref('')
    const templates = ref([])
    const loading = ref(false)

    const pagination = ref({
      sortBy: 'lastUpdateTime',
      descending: true,
      page: 1,
      rowsPerPage: 16,
      rowsNumber: 0
    })

    const onlyMy = computed({
      get(){
        const MODULE = PAGE_NAME.split('-')
        return store.state[MODULE[0]][MODULE[1]].onlyMy
      },
      set(value){
        store.commit(`${PAGE_NAME.replaceAll('-', '/')}/setOnlyMy`, value)
      }
    })

    const search = () => {
      loading.value = true
      templates.value = []
      const fnList = onlyMy.value ? templateApi.listMy : templateApi.list
      fnList({ 
        name: name.value,
        orderField: pagination.value.sortBy ? pagination.value.sortBy : "", 
        orderType: pagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: pagination.value.page, 
        pageSize: pagination.value.rowsPerPage
      }).then(resp => {
        templates.value = resp.data.page
        pagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError('查询应用发生错误')
      }).finally(() => {
        loading.value = false
      })
    }

    watch(onlyMy, () => {
      pagination.value.page = 1
      search()
    })

    onMounted(() => {
      search()

      store.commit('setTitle', { title: '应用' })
    })

    return {
      FRONTEND_SERVICE_NAME: FRONTEND_SERVICE_NAME,

      dayjs,

      name,
      onlyMy,
      templates,
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
        search(true)
      },

      onClickTemplate(templateId){
        router.push(`/workspace/template/${templateId}`)
      },

      onClickTemplateUsername(templateUsername){
        name.value = `@${templateUsername}`
        pagination.value.page = 1
        search()
      },

      onClickApplyTemplate(templateId){
        router.push(`/workspace/template/${templateId}/apply`)
      },

      onClickDeleteTemplate(name, templateId){
        $q.dialog({
          title: '删除',
          message: `确定删除应用 ${name} ？`,
          cancel: true,
        }).onOk(data => {
          templateApi.delete({ templateId }).then(resp => {
            search()
          }, resp => {
            qUtil.notifyError(resp.message || '删除发生错误')
          })
        })
      },

    }
  }
};
</script>