<template>
  <div class="script-piece-list column">
    <q-scroll-area class="col">
      <q-table
        dark
        flat
        hide-header
        hide-pagination
        :rows="pieces"
        :columns="columns"
        :pagination="{
          rowsPerPage: 0,
        }"
        :loading="loading"
        row-key="scriptPieceId"
      >
        <template v-slot:body-cell-name="props">
          <q-td :props="props" class="cursor-pointer" @click="onClickDetail(props.row.scriptPieceId)">
            {{props.value}}
          </q-td>
        </template>
        <template v-slot:body-cell-action="props">
          <q-td :props="props">
            <div>
              <q-btn v-if="!isPublic" dense flat size="sm" class="q-mr-sm" icon="delete"  color="grey"
                @click="onClickDelete(props.row.scriptPieceId, props.row.name)">
                <q-tooltip>删除</q-tooltip>
              </q-btn>
              <q-btn dense flat size="sm" icon="add" color="grey"
                @click="onClickScript(props.row.script)">
                <q-tooltip>插入</q-tooltip>
              </q-btn>
            </div>
          </q-td>
        </template>
      </q-table>
    </q-scroll-area>
    <q-separator color="grey" />
    <div>
      <q-form>
        <q-input dark borderless dense square class="q-pl-md" placeholder="搜索" v-model="name" >
          <template v-slot:append>
            <q-btn dense flat icon="search" class="cursor-pointer q-mr-xs" type="submit" @click="onClickSearch" />
          </template>
          <template v-slot:after v-if="!isPublic">
            <q-btn flat label="创建" @click="onClickAdd" />
          </template>
        </q-input>
      </q-form>
    </div>
  </div>
  
</template>

<style lang="scss">
.script-piece-list{
  .q-field__control-container {
    padding-top: initial !important;
  }
  .q-field__input, .q-field__native, .q-field__prefix, .q-field__suffix {
    padding: 6px 0 !important;
  }
  .q-field__control, .q-field__native {
    min-height: initial !important;
  }
}
</style>

<script>
import { ref, inject, onMounted, watch } from 'vue'
import scriptLibApi from '@/api/scriptlib.api'

const columns = [
  {
    name: 'name',
    label: 'name',
    field: 'name',
    align: 'left',
  },
  {
    name: 'action',
    label: 'action',
    align: 'left',
    style: 'width: 50px',
  },
]

export default {
  props: ['isPublic'],
  setup(props, { emit }){
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const loading = ref(false);
    const isPublic = ref(props.isPublic);
    const name = ref('');
    const pieces = ref([]);

    const search = () => {
      loading.value = true;
      pieces.value = [];
      scriptLibApi.list({
        isPublic: props.isPublic,
        name: name.value,
      }).then((resp) => {
        const page = resp.data.page
        pieces.value = page
      }).finally(() => {
        loading.value = false;
      })
    }

    watch(() => props.isPublic, (newValue) => {
      isPublic.value = newValue;
      search();
    })

    onMounted(() => {
      search();
    })

    return {
      loading,
      isPublic,
      name,
      columns,
      pieces,

      onClickSearch(){
        search()
      },

      onClickScript(script){
        emit('script', script)
      },

      onClickDetail(scriptPieceId){
        emit('detail', scriptPieceId)
      },

      onClickAdd(){
        emit('detail', null)
      },

      onClickDelete(scriptPieceId, name){
        $q.dialog({
          title: '删除',
          message: `确定删除脚本“${name}”？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          scriptLibApi.delete({ scriptPieceId }).then(resp => {
            search(true)
            qUtil.notifySuccess('删除完成')
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        })
      },
    }
  }
}
</script>