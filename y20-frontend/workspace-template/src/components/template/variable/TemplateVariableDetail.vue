<template>
  <div class="template-variable-detail">
    <div class="row q-pb-md">
      <div class="col page-heading self-center">查看变量</div>
    </div>
    <q-form class="q-gutter-md">
      <q-input
        outlined
        readonly
        v-model="variable.name"
        :label="`变量名 *`"
        lazy-rules
        :rules="[
          (val) => (val && val.length > 0) || '请输入变量名',
          (val) => val.length < 50 || '变量名长度不超过50个字符',
        ]"
      />

      <q-input outlined readonly v-model="variable.hint" label="描述" />

      <q-input
        outlined
        readonly
        type="number"
        :min="0"
        v-model="variable.order"
        :label="`序号`"
        hint="变量的显示顺序。序号越大，位置越靠后"
      />

      <div class="row q-gutter-y-sm">
        <template v-if="variable.kind == 'PROJECT_VARIABLE_REFERENCE'">
          <div class="col-12">
            <q-input
              outlined
              readonly
              label="引用项目变量"
              v-model="variable.value"
            />
          </div>
        </template>
        <template v-else>
          <div class="col-8 q-pr-sm">
            <q-input
              v-if="variable.paramType == 'INPUT'"
              outlined
              readonly
              :disable="variable.kind == 'IN'"
              v-model="variable.value"
              :label="`变量值 *`"
            />
            <q-input
              v-else-if="variable.paramType == 'TEXT'"
              outlined
              readonly
              type="textarea"
              :disable="variable.kind == 'IN'"
              v-model="variable.value"
              :label="`变量值 *`"
            />
            <div v-else-if="variable.paramType == 'FILE'">
              <q-input
                outlined
                readonly
                :disable="variable.kind == 'IN'"
                v-model="variable.value"
                hint="可通过上传文件设置变量值"
                :label="`变量值 *`"
              />
            </div>
            <div v-else-if="variable.paramType == 'SELECT_AGENT'">
              <q-input
                outlined
                readonly
                :label="`变量值 *`"
                hint="选择节点"
                :disable="variable.kind == 'IN'"
                v-model="variable.value"
              />
            </div>
          </div>
          <div class="col">
            <q-select
              outlined
              readonly
              emit-value
              map-options
              label="值类型"
              v-model="variable.paramType"
              :options="paramTypeOptions"
            />
          </div>
        </template>
      </div>

      <div class="q-gutter-sm">
        <q-radio class="q-ml-none" :model-value="variable.kind" val="NORMAL" label="普通" />
        <q-radio :model-value="variable.kind" val="SECRET" label="机密" />
        <q-radio :model-value="variable.kind" val="IN" label="输入参数" />
        <q-radio
          :model-value="variable.kind"
          val="PROJECT_VARIABLE_REFERENCE"
          label="从项目变量引用"
        />
      </div>
      <div v-if="variable.kind == 'IN'" class="row q-gutter-x-sm">
        <div class="self-center q-py-sm">
          <q-toggle size="xs" :model-value="variable.required" label="必填" />
        </div>
        <div class="self-center q-py-sm">
          <q-toggle size="xs" :model-value="variable.password" label="密码" />
        </div>
        <div class="col-12 col-sm-6 self-center q-py-sm">
          <q-input
            readonly
            outlined
            dense
            :disable="!variable.defaultValue"
            v-model="variable.defaultValue"
            label="默认值"
          >
            <template v-slot:before>
              <q-toggle size="xs" :model-value="!!variable.defaultValue" />
            </template>
          </q-input>
        </div>
      </div>
      
      <div class="q-pt-sm">
        <q-btn flat class="bg-grey-2" label="返回" @click="onClickBack" />
      </div>
    </q-form>
  </div>
</template>

<style lang="scss">
.template-variable-detail {
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

const paramTypeOptions = [
  {
    label: "单行文本",
    value: "INPUT",
  },
  {
    label: "多行文本",
    value: "TEXT",
  },
  {
    label: "文件",
    value: "FILE",
  },
  {
    label: "节点",
    value: "SELECT_AGENT",
  },
];

export default {
  components: {
    LayoutTwoColumn,
  },
  setup() {
    const qUtil = inject("quasarUtil")(inject("useQuasar")());
    const router = inject("useRouter")();
    const route = inject("useRoute")();
    const { templateId, templateVariableName } = route.params;

    const template = inject("template");
    const variable = computed(() => template.value.variables ? template.value.variables[templateVariableName] : {})

    return {
      templateVariableName,
      variable,

      paramTypeOptions,

      onClickBack() {
        router.push(`/workspace/template/${templateId}/variable`);
      },
    };
  },
};
</script>