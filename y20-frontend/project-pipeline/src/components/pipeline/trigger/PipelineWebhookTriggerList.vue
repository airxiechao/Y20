<template>
  <div class="pipeline-webhook-trigger-list">
    <div class="row q-pb-md">
      <div class="col page-heading self-center">Webhook 触发</div>
      <div class="self-center">
        <q-btn flat icon="add" no-caps label="创建触发" @click="onClickCreateWebhookTrigger" />
      </div>
    </div>
    <q-banner dense rounded class="q-pa-md bg-grey-2">
      <template v-slot:avatar>
        <q-icon name="info" color="primary" />
      </template>
      <q-icon class="vertical-middle q-mr-xs" name="link"/>
      <span class="vertical-middle">URL：</span>
      <span style="word-break: break-all; display: inline-block;" class="bg-white q-py-xs q-px-sm rounded-borders vertical-middle cursor-pointer">
        <span @click="onClickCopyUrl">{{webhookUrl}}</span><router-link target="_blank" to="/user/token/create">{{`<创建 Webhook 令牌>`}}</router-link>
      </span>
      <span class="vertical-middle q-ml-sm">请求方式：</span>
      <span style="word-break: break-all; display: inline-block" class="bg-white q-py-xs q-px-sm rounded-borders vertical-middle">
        POST
      </span>
      <span class="vertical-middle q-ml-sm">内容类型：</span>
      <span style="word-break: break-all; display: inline-block" class="bg-white q-py-xs q-px-sm rounded-borders vertical-middle">
        application/json
      </span>
    </q-banner>
    <q-table
      flat
      :rows="pipelineWebhookTriggers"
      :columns="columns"
      :loading="loading"
      row-key="name"
      :pagination="{
        sortBy: 'id',
        descending: false,
        rowsPerPage: 20,
      }"
      :rows-per-page-options="[5, 10, 20, 50]"
    >
      <template v-slot:body-cell-tag="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickWebhookTrigger(props.row.pipelineWebhookTriggerId)">
          <q-icon name="numbers" />y20-{{props.value}}
        </q-td>
      </template>
      <template v-slot:body-cell-sourceType="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickWebhookTrigger(props.row.pipelineWebhookTriggerId)">
          {{props.value}}
        </q-td>
      </template>
      <template v-slot:body-cell-eventType="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickWebhookTrigger(props.row.pipelineWebhookTriggerId)">
          {{props.value}}
        </q-td>
      </template>
      <template v-slot:body-cell-lastTriggerTime="props">
        <q-td :props="props" class="cursor-pointer" @click="onClickWebhookTrigger(props.row.pipelineWebhookTriggerId)">
          <span v-if="props.value" class="vertical-middle q-mr-xs">{{dayjs(props.value).format('YYYY-MM-DD HH:mm:ss')}}</span>
          <q-icon v-if="props.row.lastTriggerPipelineRunId" class="vertical-middle cursor-pointer" name="history" @click="onClickLastTrigger(props.row.lastTriggerPipelineRunId)" />
        </q-td>
      </template>
      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-btn icon="delete" flat dense color="negative" label="" @click="onClickDeleteWebhookTrigger(props.row.tag, props.row.pipelineWebhookTriggerId)" />
        </q-td>
      </template>
    </q-table>
  </div>
</template>

<style lang="scss">
.pipeline-webhook-trigger-list{
  
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
import { LayoutTwoColumn } from 'common'
import { ref, inject, computed, onMounted } from 'vue'
import { copyToClipboard } from 'quasar'

import pipelineApi from '@/api/pipeline.api'

const columns = [
  {
    name: 'tag',
    required: true,
    label: '匹配标签',
    align: 'left',
    field: 'tag',
    format: val => `${val}`,
    sortable: true,
  },
  {
    name: 'sourceType',
    required: false,
    label: '触发来源',
    align: 'left',
    field: 'sourceType',
    format: val => `${val[0]}${val.toLowerCase().slice(1)}`,
  },
  {
    name: 'eventType',
    required: false,
    label: '事件类型',
    align: 'left',
    field: 'eventType',
    format: val => `${val[0]}${val.toLowerCase().slice(1)}`,
  },
  {
    name: 'lastTriggerTime',
    required: false,
    label: '最后触发',
    align: 'left',
    field: 'lastTriggerTime',
  },
  {
    name: 'action',
    headerStyle: "width: 5%;",
  }
]

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const router = inject('useRouter')()
    const route = inject('useRoute')()
    const projectName = inject('projectName')
    const { projectId, pipelineId } = route.params

    const pipelineWebhookTriggers = ref([])
    const loading = ref(false)
    const webhookUrl = ref(`${window.location.protocol}//${window.location.host}/pipeline/api/webhook/pipeline/trigger?auth=`)

    const search = () => {
      loading.value = true
      pipelineApi.listWebhookTrigger({ pipelineId }).then(resp => {
        const data = resp.data.page
        pipelineWebhookTriggers.value = data
      }, resp => {
        
      }).finally(() => {
        loading.value = false
      })
    }
    
    onMounted(() => {
      search()
    })

    return {
      dayjs,
      projectName,

      columns,
      loading,
      pipelineWebhookTriggers,
      webhookUrl,

      onClickCreateWebhookTrigger(){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/trigger/create`)
      },

      onClickWebhookTrigger(pipelineWebhookTriggerId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/trigger/${pipelineWebhookTriggerId}`)
      },

      onClickLastTrigger(lastTriggerPipelineRunId){
        router.push(`/project/${projectId}/pipeline/${pipelineId}/run/${lastTriggerPipelineRunId}`)
      },

      onClickDeleteWebhookTrigger(tag, pipelineWebhookTriggerId){
        $q.dialog({
          title: '删除',
          message: `确定删除触发 #y20-${tag} ？`,
          cancel: true,
        }).onOk(data => {
          pipelineApi.deleteWebhookTrigger({ pipelineId, pipelineWebhookTriggerId }).then(resp => {
            search()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },

      onClickCopyUrl(){
        copyToClipboard(webhookUrl.value).then(() => {
          qUtil.notifySuccess('已复制到粘贴板')
        }).catch(() => {
          qUtil.notifyError('复制到粘贴板发生错误')
        })
      },

    }
  }
};
</script>