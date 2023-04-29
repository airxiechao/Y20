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
        <div class="q-pa-xs page-content">
          <div class="page-heading q-px-xs">
            <div class="row">
              <div class="self-center">应用市场</div>
              <q-space />
              <div class="self-center">
                <q-toggle class="text-subtitle2" size="xs" v-model="onlyMy" label="我发布的" />
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
            <template v-slot:no-data>
              <div class="full-width">
                <div class="row q-mt-xs">
                  <div class="col-12">
                    <q-card flat class="template-card q-pa-xs text-center">
                      <q-item>
                        <q-item-section>
                          <q-item-label>
                            <q-avatar icon="extension" class="q-ma-sm text-grey" size="lg" style="background: #ECF2FF;" />
                          </q-item-label>
                          <q-item-label>
                            <span class="vertical-middle text-grey">没有应用</span>
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-card>
                  </div>
                </div>
              </div>
            </template>
            <template v-slot:loading>
              <div class="row">
                <div class="q-pa-xs col-12 col-md-6 col-lg-3" v-for="i in [...Array(pagination.rowsPerPage).keys()]" :key="i">
                  <q-card flat class="template-card">
                    <q-list class="q-pa-xs">
                      <q-item>
                        <q-item-section avatar>
                          <q-avatar icon="extension" class="q-mr-sm text-grey" size="lg" style="background: #ECF2FF;" />
                        </q-item-section>
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
              <div class="q-pa-xs col-12 col-md-6 col-lg-3">
                <q-card flat class="template-card full-height cursor-pointer" @click="onClickTemplate(props.row.templateId)">
                  <q-list class="q-pa-xs">
                    <q-item>
                      <q-item-section avatar>
                        <q-avatar icon="extension" class="q-mr-sm text-primary" size="lg" style="background: #ECF2FF;" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label>
                          <span class="title">
                            {{props.row.name}}
                          </span>
                        </q-item-label>
                        <q-item-label lines="1" style="word-break: break-all;" caption>
                          <q-icon name="download" />
                          <span class="q-mr-xs">{{props.row.numApply || 0}}</span>
                          <q-icon name="person" />
                          <span class="cursor-pointer" @click.stop="onClickTemplateUsername(props.row.username)">{{props.row.username}}</span>
                        </q-item-label>
                      </q-item-section>
                      <q-item-section side>
                        <q-btn flat dense round icon="more_vert" @click.stop>
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

          <div class="page-heading q-px-md q-mt-xs">
            <span class="vertical-middle">热门推荐</span>
          </div>
          <div class="row q-px-sm q-pb-sm">
            <div class="self-center">
              <q-btn dense flat round icon="arrow_left" 
                :disable="recommendPagination.page <= 1"
                :color="recommendPagination.page <= 1 ? 'grey': ''"
                @click="onClickRecommendPrevious" />
            </div>
            <div class="q-px-xs" style="flex: 1">
              <q-table
                grid
                :hide-bottom="recommendLoading"
                hide-pagination
                :rows="recommends"
                v-model:pagination="recommendPagination"
                row-key="templateId"
                :loading="recommendLoading"
              >
                <template v-slot:no-data>
                  <div class="full-width">
                    <div class="row">
                      <div class="col-12">
                        <q-card flat class="template-card q-pb-xs text-center">
                          <q-item>
                            <q-item-section>
                              <q-item-label>
                                <q-avatar icon="extension" class="q-my-sm text-grey" size="lg" style="background: #ECF2FF;" />
                              </q-item-label>
                              <q-item-label>
                                <span class="vertical-middle text-grey">没有推荐</span>
                              </q-item-label>
                            </q-item-section>
                          </q-item>
                        </q-card>
                      </div>
                    </div>
                  </div>
                </template>
                <template v-slot:loading>
                  <div class="row">
                    <div class="q-pa-xs col-12 col-md-6 col-lg-3" v-for="i in [1,2,3,4]" :key="i">
                      <q-card flat class="template-card">
                        <q-list class="q-pa-xs">
                          <q-item>
                            <q-item-section>
                              <q-item-label>
                                <q-skeleton type="text" animation="fade" />
                              </q-item-label>
                              <q-item-label caption>
                                <q-skeleton type="text" animation="fade" />
                              </q-item-label>
                            </q-item-section>
                          </q-item>
                        </q-list>
                      </q-card>
                    </div>
                  </div>
                </template>
                <template v-slot:item="props">
                  <div class="q-pa-xs col-12 col-md-6 col-lg-3">
                    <q-card flat class="template-card full-height cursor-pointer" @click="onClickTemplate(props.row.templateId)">
                      <q-list class="q-pa-xs">
                        <q-item>
                          <q-item-section>
                            <q-item-label>
                              <span class="title">
                                {{props.row.name}}
                              </span>
                            </q-item-label>
                            <q-item-label lines="1" style="word-break: break-all;" caption>
                              <q-icon name="download" />
                              <span class="q-mr-xs">{{props.row.numApply || 0}}</span>
                              <q-icon name="person" />
                              <span>{{props.row.username}}</span>
                            </q-item-label>
                          </q-item-section>
                        </q-item>
                      </q-list>
                    </q-card>
                  </div>
                </template>
              </q-table>
            </div>
            <div class="self-center">
              <q-btn dense flat round icon="arrow_right" 
                :disable="recommendPagination.page * recommendPagination.rowsPerPage >= recommendPagination.rowsNumber"
                :color="recommendPagination.page * recommendPagination.rowsPerPage >= recommendPagination.rowsNumber ? 'grey': ''"
                @click="onClickRecommendNext" />
            </div>
          </div>

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
    .q-item{
      padding: 8px;
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
    const recommends = ref([])
    const recommendLoading = ref(false)

    const pagination = ref({
      sortBy: 'lastUpdateTime',
      descending: true,
      page: 1,
      rowsPerPage: 16,
      rowsNumber: 0
    })

    const recommendPagination = ref({
      sortBy: 'numApply',
      descending: true,
      page: 1,
      rowsPerPage: 4,
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

    const search = (noLoading) => {
      if(!noLoading){
        loading.value = true
        templates.value = []
      }
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

    const recommend = (noLoading) => {
      if(!noLoading){
        recommendLoading.value = true
        recommends.value = []
      }
      templateApi.recommend({ 
        orderField: recommendPagination.value.sortBy ? recommendPagination.value.sortBy : "", 
        orderType: recommendPagination.value.descending ? 'DESC' : 'ASC', 
        pageNo: recommendPagination.value.page, 
        pageSize: recommendPagination.value.rowsPerPage
      }).then(resp => {
        recommends.value = resp.data.page
        recommendPagination.value.rowsNumber = resp.data.total
      }, resp => {
        qUtil.notifyError('查询推荐发生错误')
      }).finally(() => {
        recommendLoading.value = false
      })
    }

    watch(onlyMy, () => {
      pagination.value.page = 1
      search()
    })

    onMounted(() => {
      search()
      recommend()

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
      recommends,
      recommendLoading,
      recommendPagination,

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

      onClickRecommendPrevious(){
        if(recommendPagination.value.page > 1){
          recommendPagination.value.page -= 1
          recommend()
        }
      },

      onClickRecommendNext(){
        if(recommendPagination.value.page * recommendPagination.value.rowsPerPage < recommendPagination.value.rowsNumber){
          recommendPagination.value.page += 1
          recommend()
        }
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
            search(true)
          }, resp => {
            qUtil.notifyError(resp.message || '删除发生错误')
          })
        })
      },

    }
  }
};
</script>