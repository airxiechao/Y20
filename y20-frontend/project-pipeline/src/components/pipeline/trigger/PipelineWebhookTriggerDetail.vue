<template>
  <div class="pipeline-webhook-trigger-detail">
    <div class="page-heading q-pb-md">
      <template v-if="isEdit">编辑 Webhook 触发</template>
      <template v-else>创建 Webhook 触发</template>
    </div>
    <q-form @submit="onSubmit" class="q-gutter-md">
      <q-select
        outlined
        map-options
        emit-value
        v-model="trigger.sourceType"
        :options="sourceTypeOptions"
        :label="`触发来源 *`"
        hint="发起 Webhook 的系统"
        lazy-rules
        :rules="[
          (val) => !!val || '请选择触发来源',
        ]"
      />

      <q-select
        outlined
        map-options
        emit-value
        v-model="trigger.eventType"
        :options="eventTypeOptions"
        :label="`事件类型 *`"
        hint="触发 Webhook 的事件类型"
        lazy-rules
        :rules="[
          (val) => !!val || '请选择事件类型',
        ]"
      />

      <q-input
        outlined
        stack-label
        v-model="trigger.tag"
        :label="`匹配标签 *`"
        prefix="#y20-"
        hint="事件中的文字信息（比如：Push 事件的 commit 信息）含有标签（#y20-...）才会启动流水线。"
        lazy-rules
        :rules="[
          (val) => (val && val.length > 0) || '请输入匹配标签',
          (val) => val.length <= 50 || '匹配标签长度不超过50个字符',
        ]"
      />

      <div class="text-grey q-pl-xs">
        <q-icon name="info" color="primary" class="q-ml-sm vertical-middle q-mr-xs" />
        <span class="vertical-middle">标签中可以使用 {{'{{'}}...}} 进行位置匹配，并在输入变量通过 {{'{{'}}...}} 引用</span>
      </div>

      <PipelineInVariablesForm :pipelineInVariables="pipelineInVariables" :loading="loading" />

      <div>
        <q-btn v-if="isEdit" unelevated label="保存" type="submit" color="primary" />
        <q-btn v-else unelevated label="创建" type="submit" color="primary" />
        <q-btn flat class="q-ml-sm bg-grey-2" label="返回" @click="onClickCancel" />
      </div>
    </q-form>
  </div>
</template>

<style lang="scss">
.pipeline-webhook-trigger-detail {
}
</style>

<i18n>
{
  "zhCN": {
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from "common";
import { ref, inject, computed, watch, onMounted } from "vue";
import PipelineInVariablesForm from '@/components/pipeline/common/PipelineInVariablesForm'

import pipelineApi from "@/api/pipeline.api";

const sourceTypeOptions = [
  {
    label: "Github",
    value: "GITHUB",
  },
  {
    label: "Gitlab",
    value: "GITLAB",
  },
  {
    label: "Coding",
    value: "CODING",
  },
  {
    label: "Gitee",
    value: "GITEE",
  },
];

const eventTypeOptions = [
  {
    label: "Push",
    value: "PUSH",
  },
];

export default {
  components: {
    LayoutTwoColumn,
    PipelineInVariablesForm,
  },
  setup() {
    const qUtil = inject("quasarUtil")(inject("useQuasar")());
    const router = inject("useRouter")();
    const route = inject("useRoute")();
    const projectName = inject("projectName");
    const { projectId, pipelineId, pipelineWebhookTriggerId } = route.params;

    const loading = ref(false);
    const isEdit = computed(() => !!pipelineWebhookTriggerId);

    const trigger = ref({
      sourceType: null,
      eventType: null,
      tag: null,
      inParams: {}
    });

    const pipelineInVariables = ref([])

    const inParams = computed(() => {
      let params = {}
      pipelineInVariables.value.forEach(v => {
        params[v.name] = v.value
      })
      return params
    })

    onMounted(() => {
      loading.value = true
      pipelineApi.listVariable({ pipelineId }).then(resp => {
        pipelineInVariables.value = Object.values(resp.data).sort((a,b) => (a.order || 0) - (b.order || 0)).filter(v => v.kind == 'IN')
        pipelineInVariables.value.forEach(v => {
          v.value = v.defaultValue || null
        })

        if (isEdit.value) {
          pipelineApi.getWebhookTrigger({ pipelineId, pipelineWebhookTriggerId }).then((resp) => {
            trigger.value = resp.data
            const triggerInParams = resp.data.inParams
            if(triggerInParams){
              Object.keys(triggerInParams).forEach(k => {
                const i = pipelineInVariables.value.findIndex(v => v.name === k)
                if(i >= 0){
                  pipelineInVariables.value[i].value = triggerInParams[k]
                }
              })
            }
          });
        }
      }).finally(() => {
        loading.value = false
      })
    });

    return {
      projectName,
      pipelineWebhookTriggerId,
      trigger,

      pipelineInVariables,
      inParams,
      loading,

      sourceTypeOptions,
      eventTypeOptions,

      isEdit,

      onClickCancel() {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/trigger`);
      },

      onSubmit() {
        if (isEdit.value) {
          pipelineApi.updateWebhookTrigger({ 
            pipelineId,
            pipelineWebhookTriggerId,
            sourceType: trigger.value.sourceType,
            eventType: trigger.value.eventType,
            tag: trigger.value.tag,
            inParams: inParams.value,
          }).then(
            (resp) => {
              router.push(
                `/project/${projectId}/pipeline/${pipelineId}/trigger`
              );
            },
            (resp) => {
              qUtil.notifyError(resp.message);
            }
          );
        } else {
          pipelineApi.createWebhookTrigger({ 
            pipelineId,
            sourceType: trigger.value.sourceType,
            eventType: trigger.value.eventType,
            tag: trigger.value.tag,
            inParams: inParams.value,
          }).then(
            (resp) => {
              router.push(
                `/project/${projectId}/pipeline/${pipelineId}/trigger`
              );
            },
            (resp) => {
              qUtil.notifyError(resp.message);
            }
          );
        }
      },
    };
  },
};
</script>