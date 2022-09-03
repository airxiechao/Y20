<template>
  <div class="pipeline-variable-detail">
    <div class="page-heading text-primary">
      <template v-if="isEdit">编辑变量</template>
      <template v-else>创建变量</template>
    </div>
    <q-form @submit="onSubmit" class="q-mt-md q-gutter-md">
      <q-input
        :disable="isEdit"
        outlined
        v-model="variable.name"
        :label="`变量名 *`"
        lazy-rules
        :rules="[
          (val) => (val && val.length > 0) || '请输入变量名',
          (val) => val.length <= 50 || '变量名长度不超过50个字符',
          (val) => /^[a-zA-Z0-9-_]{1,50}$/.test(val) || '变量名只能包含：a-zA-Z0-9-_'
        ]"
      />

      <q-input outlined v-model="variable.hint" label="描述" hint="变量描述" />

      <q-input
        outlined
        type="number"
        :min="0"
        v-model="variable.order"
        :label="`序号`"
        hint="变量的显示顺序。序号越大，位置越靠后"
      />

      <div class="row q-gutter-y-sm">
        <template v-if="variable.kind == 'PROJECT_VARIABLE_REFERENCE'">
          <div class="col-12">
            <q-select
              filled
              label="引用项目变量 *"
              v-model="variable.value"
              :options="projectVariableNames"
              lazy-rules
              :rules="[
                (val) => !!val || '请选择项目变量',
              ]"
            />
          </div>
        </template>
        <template v-else>
          <div class="col-8 q-pr-sm">
            <q-input
              v-if="variable.paramType == 'INPUT'"
              outlined
              :disable="variable.kind == 'IN'"
              v-model="variable.value"
              :label="`变量值 *`"
              lazy-rules
              :rules="[
                (val) => !!val || '请输入变量值',
              ]"
            />
            <q-input
              v-else-if="variable.paramType == 'TEXT'"
              type="textarea"
              :disable="variable.kind == 'IN'"
              outlined
              v-model="variable.value"
              :label="`变量值 *`"
              lazy-rules
              :rules="[
                (val) => !!val || '请输入变量值',
              ]"
            />
            <div v-else-if="variable.paramType == 'SELECT'">
              <q-select
                :disable="variable.kind == 'IN'"
                outlined
                v-model="variable.value"
                :label="`变量值 *`"
                :options="variable.options"
                lazy-rules
                :rules="[
                  (val) => !!val || '请输入变量值',
                ]"
              />
              <q-select
                class="q-mt-md q-ml-md"
                label="添加可选项"
                stack-label
                filled
                v-model="variable.options"
                use-input
                use-chips
                multiple
                hide-dropdown-icon
                new-value-mode="add-unique"
              />
            </div>
            <div v-else-if="variable.paramType == 'FILE'">
              <q-input
                :disable="variable.kind == 'IN'"
                outlined
                v-model="variable.value"
                hint="可通过上传文件设置变量值"
                :label="`变量值 *`"
                lazy-rules
                :rules="[
                  (val) => !!val || '请输入变量值',
                ]"
              />
              <q-uploader
                class="q-mt-md q-ml-md"
                label="上传文件"
                :disable="variable.kind == 'IN'"
                :url="uploadPipelineTempFileUrl"
                :headers="uploadPipelineTempFileHeaders"
                :formFields="uploadPipelineTempFileFields"
                fieldName="file"
                auto-upload
                @uploaded="onUploaded"
              />
            </div>
            <div v-else-if="variable.paramType == 'SELECT_AGENT'">
              <SelectAgent
                :disable="variable.kind == 'IN'"
                v-model="variable.value"
                lazy-rules
                :label="`变量值 *`"
                :rules="[
                  (val) => !!val || '请选择节点',
                ]"
              />
            </div>
          </div>
          <div class="col">
            <q-select
              filled
              emit-value
              map-options
              label="值类型"
              v-model="variable.paramType"
              :options="paramTypeOptions"
            />
          </div>
        </template>
      </div>

      <div class="q-gutter-x-sm">
        <q-radio class="q-ml-none" v-model="variable.kind" val="NORMAL" label="普通" />
        <q-radio v-model="variable.kind" val="SECRET" label="机密" />
        <q-radio v-model="variable.kind" val="IN" label="输入参数" />
        <q-radio
          v-model="variable.kind"
          val="PROJECT_VARIABLE_REFERENCE"
          label="从项目变量引用"
        />
      </div>
      <div v-if="variable.kind == 'IN'" class="row q-gutter-x-sm">
        <div class="self-center q-py-sm">
          <q-toggle v-model="variable.required" label="必填" />
        </div>
        <div class="self-center q-py-sm">
          <q-toggle v-model="variable.password" label="密码" />
        </div>
        <div class="col-12 col-sm-6 self-center q-py-sm">
          <q-input
            outlined
            bg-color="grey-2"
            dense
            :disable="!hasDefaultValue"
            v-model="variable.defaultValue"
            label="默认值"
          >
            <template v-slot:before>
              <q-toggle v-model="hasDefaultValue" />
            </template>
          </q-input>
        </div>
      </div>

      <div class="q-pt-sm">
        <q-btn v-if="isEdit" unelevated label="保存" type="submit" color="primary" />
        <q-btn v-else unelevated label="创建" type="submit" color="primary" />
        <q-btn flat class="q-ml-sm" label="返回" @click="onClickCancel" />
      </div>
    </q-form>
  </div>
</template>

<style lang="scss">
.pipeline-variable-detail {
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
import PipelineTempFileUpload from "@/components/pipeline/file/mixin/PipelineTempFileUpload.mixin";
import SelectAgent from "@/components/pipeline/common/SelectAgent";

import projectApi from "@/api/project.api";
import pipelineApi from "@/api/pipeline.api";

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
    label: "单项选择",
    value: "SELECT",
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
  mixins: [PipelineTempFileUpload],
  components: {
    LayoutTwoColumn,
    SelectAgent,
  },
  setup() {
    const qUtil = inject("quasarUtil")(inject("useQuasar")());
    const router = inject("useRouter")();
    const route = inject("useRoute")();
    const projectName = inject("projectName");
    const { projectId, pipelineId, pipelineVariableName } = route.params;

    const isEdit = computed(() => !!pipelineVariableName);

    const variable = ref({
      name: null,
      hint: null,
      value: null,
      paramType: "INPUT",
      kind: "NORMAL",
      required: false,
      password: false,
      order: null,
      defaultValue: null,
      options: null,
    });

    const hasDefaultValue = ref(false);

    const projectVariableNames = ref([]);

    watch(hasDefaultValue, (newValue) => {
      if (!newValue) {
        variable.value.defaultValue = null;
      }
    });

    watch(
      () => variable.value.defaultValue,
      (newValue, oldValue) => {
        if (newValue) {
          hasDefaultValue.value = true;
        }
      }
    );

    watch(
      () => variable.value.paramType,
      (newValue, oldValue) => {
        variable.value.value = null
        if(newValue != "SELECT") {
          variable.value.options = null
        }
      }
    );

    watch(
      () => variable.value.kind,
      (newValue, oldValue) => {
        if(newValue == "PROJECT_VARIABLE_REFERENCE") {
          projectApi.listVariable({ projectId }).then((resp) => {
            projectVariableNames.value = Object.keys(resp.data);
          });
        } else if(newValue == "IN") {
          variable.value.value = null;
        }
      }
    );

    onMounted(() => {
      if (isEdit.value) {
        pipelineApi
          .getVariable({ pipelineId, name: pipelineVariableName })
          .then((resp) => {
            variable.value = resp.data;
          });
      }
    });

    return {
      projectName,
      pipelineVariableName,
      variable,
      hasDefaultValue,

      paramTypeOptions,
      isEdit,
      projectVariableNames,

      onUploaded({ files, xhr }) {
        let resp = JSON.parse(xhr.response);
        variable.value.value = resp.data;
      },

      onClickCancel() {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/variable`);
      },

      onSubmit() {
        if (isEdit.value) {
          pipelineApi.updateVariable({ pipelineId, ...variable.value }).then(
            (resp) => {
              router.push(
                `/project/${projectId}/pipeline/${pipelineId}/variable`
              );
            },
            (resp) => {
              qUtil.notifyError(resp.message);
            }
          );
        } else {
          pipelineApi.createVariable({ pipelineId, ...variable.value }).then(
            (resp) => {
              router.push(
                `/project/${projectId}/pipeline/${pipelineId}/variable`
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