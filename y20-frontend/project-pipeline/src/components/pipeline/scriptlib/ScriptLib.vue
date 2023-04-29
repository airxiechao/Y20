<template>
  <div class="script-lib full-width q-mt-sm column">
    <q-tabs
      v-model="tab"
      dense      
      align="left"
      :breakpoint="0"
      class="text-grey"
      active-color="white"
      indicator-color="white"
      narrow-indicator
    >
      <q-tab name="public" label="公共脚本" />
      <q-tab name="private" label="我的脚本" />
    </q-tabs>

    <q-separator color="grey" />

    <ScriptPieceList v-if="mode == 'list'" 
      class="col"
      :isPublic="tab == 'public'" 
      @script="onClickScript"
      @detail="onClickListDetail"
    />
    <ScriptPieceDetail v-else-if="mode == 'detail'" 
      class="col" 
      :isPublic="tab == 'public'" 
      :scriptPieceId="scriptPieceId" 
      @script="onClickScript"
      @back="onClickDetailBack"
    />
  </div>
</template>

<style lang="scss">
.script-lib{
  border: 1px solid #ccc;
  background: #2b2b2b;
  color: #fff;
}
</style>

<script>
import { ref, watch } from 'vue'
import ScriptPieceList from './ScriptPieceList.vue'
import ScriptPieceDetail from './ScriptPieceDetail.vue'

export default {
  components: {
    ScriptPieceList,
    ScriptPieceDetail,
  },
  setup(props, { emit }){
    const tab = ref('public');
    const mode = ref('list');
    const scriptPieceId = ref(null);

    const toListMode = () => {
      scriptPieceId.value = null;
      mode.value = 'list';
    };

    const toDetailModel = (id) => {
      scriptPieceId.value = id;
      mode.value = 'detail';
    };

    watch(tab, () => {
      toListMode();
    });

    return {
      tab,
      mode,
      scriptPieceId,

      onClickScript(script){
        emit('script', script)
      },

      onClickListDetail(id){
        toDetailModel(id);
      },

      onClickDetailBack(){
        toListMode();
      },
    }
  }
}
</script>