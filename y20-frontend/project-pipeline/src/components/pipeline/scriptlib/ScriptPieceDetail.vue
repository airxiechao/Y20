<template>
  <div class="script-piece-detail column">
    <div class="col column q-pt-sm q-px-sm">
      <template v-if="loading">
        <div><q-skeleton dark type="text" animation="none" /></div>
        <div class="col"><q-skeleton dark animation="none" height="50px" /></div>
      </template>
      <q-form v-else class="col column" @submit="onSubmit">
        <div class="text-subtitle text-grey q-pl-sm">名称</div>
        <q-input dark dense filled class="q-mt-xs q-mb-sm q-px-sm" 
          :readonly="isPublic"
          v-model="name" 
          lazy-rules
          :rules="[ 
              val => val && val.length > 0 || '请输入脚本名称',
              val => val.length <= 200 || '脚本名称长度不能超过200个字符'
          ]"
        />
        <div class="text-subtitle text-grey q-mt-xs q-pl-sm">脚本</div>
        <q-scroll-area class="col q-mt-xs q-pb-sm">
          <q-input type="textarea" class="q-px-sm" dark filled autogrow 
            :readonly="isPublic" 
            v-model="script" 
            lazy-rules
            :rules="[ 
                val => val && val.length > 0 || '请输入脚本内容'
            ]"
          />
        </q-scroll-area>
        <div class="q-pb-xs text-right row">
          <q-btn v-if="scriptPieceId" unelevated class="q-px-sm" color="grey" flat dense 
            label="插入" 
            @click="onClickScript" />
          <q-space />
          <q-btn v-if="!isPublic" unelevated class="q-px-sm" color="grey" flat dense
            :label="scriptPieceId ? '保存' : '创建'" 
            :loading="submitLoading"
            type="submit"
          />
          <q-btn color="grey" class="q-px-sm" flat dense label="返回" @click="onClickBack" />
        </div>
      </q-form>
    </div>
  </div>
  
</template>

<style lang="scss">
.script-piece-detail{
  .q-field__control-container {
    padding-top: initial !important;
  }
  .q-field__input, .q-field__native, .q-field__prefix, .q-field__suffix {
    padding: 8px 0 !important;
  }
  .q-field__control, .q-field__native {
    min-height: initial !important;
  }
}
</style>

<script>
import { ref, inject, onMounted } from 'vue'
import scriptLibApi from '@/api/scriptlib.api'

export default {
  props: ['isPublic', 'scriptPieceId'],
  setup(props, { emit }){
    const $q = inject('useQuasar')();
    const qUtil = inject('quasarUtil')($q);
    const loading = ref(false);
    const submitLoading = ref(false);
    const isPublic = ref(props.isPublic);
    const scriptPieceId = ref(props.scriptPieceId);
    const name = ref('');
    const script = ref('');

    onMounted(() => {
      if(scriptPieceId.value){
        loading.value = true;
        scriptLibApi.get({
          isPublic: isPublic.value,
          scriptPieceId: scriptPieceId.value,
        }).then((resp) => {
          const data = resp.data;
          name.value = data.name;
          script.value = data.script;
        }).finally(() => {
          loading.value = false;
        });
      }
    })

    return {
      loading,
      submitLoading,
      isPublic,
      scriptPieceId,
      name,
      script,

      onSubmit(){
        if(isPublic.value){
          return;
        }

        if(!scriptPieceId.value){
          submitLoading.value = true;
          scriptLibApi.create({
            name: name.value,
            script: script.value,
          }).then((resp) => {
            emit('back')
          }, (resp) => {
            qUtil.notifyError(resp.message)
          }).finally(() => {
            submitLoading.value = false;
          });
        }else{
          submitLoading.value = true;
          scriptLibApi.update({
            scriptPieceId: scriptPieceId.value,
            name: name.value,
            script: script.value,
          }).then((resp) => {
            emit('back')
          }, (resp) => {
            qUtil.notifyError(resp.message)
          }).finally(() => {
            submitLoading.value = false;
          });
        }
      },

      onClickScript(){
        emit('script', script.value)
      },

      onClickBack(){
        emit('back')
      },
    }
  }
}
</script>