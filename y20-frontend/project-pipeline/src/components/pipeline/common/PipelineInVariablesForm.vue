<template>
  <q-card flat class="q-pt-sm q-gutter-y-md relative-position">
    <div class="text-primary text-bold q-mt-none">输入变量</div>
    <q-card v-if="loading" flat class="q-pa-sm">
      <div class="row q-mb-sm" v-for="pi in [1,2,3]" :key="pi">
        <div class="q-pr-sm" style="width: 80px;"><q-skeleton type="text" animation="fade" /></div>
        <div class="col"><q-skeleton type="text" animation="fade" /></div>
      </div>
    </q-card>
    <div v-else-if="!pipelineInVariables || pipelineInVariables.length == 0">
      <div class="q-px-md q-py-sm rounded-borders bg-grey-2 text-grey-7" >
        无
      </div>
    </div>
    <template
      v-else
      v-for="variable in pipelineInVariables"
      :key="variable.name"
    >
      <q-input
        bg-color="white"
        v-if="variable.paramType == 'INPUT'"
        :type="variable.password && !showPasswords[variable.name]?'password':'text'"
        :autocomplete="variable.password?'new-password':''"
        outlined
        v-model="variable.value"
        :label="`${variable.name}${variable.required ? ' *' : ''}`"
        :hint="variable.hint"
        :readonly="readonly"
        lazy-rules
        :rules="[
          (val) =>
            !variable.required || (val && val.length > 0) || `此参数为必填项！${variable.hint || ''}`,
        ]"
      >
        <template v-if="variable.password" v-slot:append>
          <q-icon 
            :name="showPasswords[variable.name] ? 'visibility': 'visibility_off'" 
            class="cursor-pointer" 
            @click="toggleShowPassword(variable.name)" 
          />
        </template>
      </q-input>
      <q-input
        bg-color="white"
        v-else-if="variable.paramType == 'TEXT'"
        type="textarea"
        outlined
        v-model="variable.value"
        :label="`${variable.name}${variable.required ? ' *' : ''}`"
        :hint="variable.hint"
        :readonly="readonly"
        lazy-rules
        :rules="[
          (val) =>
            !variable.required || (val && val.length > 0) || `此参数为必填项！${variable.hint || ''}`,
        ]"
      />
      <q-select
        bg-color="white"
        v-else-if="variable.paramType == 'SELECT'"
        outlined
        v-model="variable.value"
        :options="variable.options"
        :label="`${variable.name}${variable.required ? ' *' : ''}`"
        :hint="variable.hint"
        :readonly="readonly"
        lazy-rules
        :rules="[
          (val) =>
            !variable.required || (val && val.length > 0) || `此参数为必填项！${variable.hint || ''}`,
        ]"
      />
      <div v-else-if="variable.paramType == 'FILE'">
        <q-input
          bg-color="white"
          outlined
          v-model="variable.value"
          :label="`${variable.name}${variable.required ? ' *' : ''}`"
          :hint="variable.hint"
          :readonly="readonly"
          lazy-rules
          :rules="[
            (val) =>
              !variable.required || (val && val.length > 0) || `此参数为必填项！${variable.hint || ''}`,
          ]"
        />
        <q-uploader
          class="q-mt-md q-ml-md"
          label="上传文件"
          :url="uploadPipelineTempFileUrl"
          :headers="uploadPipelineTempFileHeaders"
          :formFields="uploadPipelineTempFileFields"
          :readonly="readonly"
          fieldName="file"
          auto-upload
          @uploaded="(info) => onUploaded(info, variable.name)"
        />
      </div>
      <SelectAgent
        bg-color="white"
        v-else-if="variable.paramType == 'SELECT_AGENT'"
        v-model="variable.value"
        :label="`${variable.name}${variable.required ? ' *' : ''}`"
        :hint="variable.hint"
        :readonly="readonly"
        lazy-rules
        :rules="[
          (val) =>
            !variable.required || (val && val.length > 0) || `此参数为必填项！${variable.hint || ''}`,
        ]"
      />
    </template>

    <!-- <q-inner-loading :showing="loading">
      <q-spinner-gears size="30px" color="primary" />
    </q-inner-loading> -->
  </q-card>
</template>

<script>
import { ref } from 'vue'
import SelectAgent from '@/components/pipeline/common/SelectAgent'
import PipelineTempFileUpload from "@/components/pipeline/file/mixin/PipelineTempFileUpload.mixin"

export default {
  mixins: [PipelineTempFileUpload],
  components: {
    SelectAgent,
  },
  props: {
    pipelineInVariables: {
      type: Array,
      default: [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  setup(props){
    const showPasswords = ref({})

    props.pipelineInVariables.forEach(v => {
      if(v.password){
        showPasswords[v.name] = false
      }
    })
    
    return {
      showPasswords,

      toggleShowPassword(name){
        showPasswords.value[name] = !showPasswords.value[name]
      },

      onUploaded({ files, xhr }, name){
        let resp = JSON.parse(xhr.response)
        props.pipelineInVariables.forEach(v => {
          if(v.name === name){
            v.value = resp.data
          }
        })
      },
    }
  },
}
</script>