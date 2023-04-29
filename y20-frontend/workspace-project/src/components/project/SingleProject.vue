<template>
  <LayoutTwoColumn class="single-project">
    <template v-slot:center>
      <div class="page-center">
        <div class="page-toolbar">
          <q-toolbar>
            <q-btn unelevated rounded flat color="primary" icon="keyboard_backspace" label="返回" @click="onClickBack" />
          </q-toolbar>
        </div>
        <div class="q-pa-sm page-content">
          <q-card flat class="q-pa-md">
            <div v-if="projectId" class="q-pb-md page-heading">修改项目</div>
            <div v-else class="q-pb-md page-heading">创建新项目</div>
            <q-form
              @submit="onSubmit"
              class="q-gutter-md"
            >
              <q-input
                outlined
                v-model="name"
                :label="`${$t('label-name')} *`"
                :hint="$t('error-name-too-long')"
                lazy-rules
                :rules="[ 
                    val => val && val.length > 0 || $t('error-no-name'),
                    val => val.length < 20 || $t('error-name-too-long')
                ]"
              />

              <div>
                <q-btn unelevated v-if="projectId" :label="$t('label-save')" type="submit" color="primary"/>
                <q-btn unelevated v-else :label="$t('label-create')" type="submit" color="primary"/>
                <q-btn flat class="q-ml-sm bg-grey-2" :label="$t('label-cancel')" @click="onClickBack" />
              </div>
            </q-form>
          </q-card>
        </div>
      </div>
    </template>
    <template v-slot:right>
      
    </template>
  </LayoutTwoColumn>
</template>

<style lang="scss">
.single-project{
  
}
</style>

<i18n>
{
  "zhCN": {
    "label-name": "名称",
    "error-no-name": "请输入项目名称",
    "error-name-too-long": "名称长度不超过20个字符",
    "label-save": "保存",
    "label-create": "创建",
    "label-cancel": "取消",
  }
}
</i18n>

<script>
import { LayoutTwoColumn } from 'common'
import { ref, inject, onMounted } from 'vue'
import projectApi from '@/api/project.api'

export default {
  components: {
    LayoutTwoColumn,
  },
  setup(){
    const qUtil = inject('quasarUtil')(inject('useQuasar')())
    const route = inject('useRoute')()
    const router = inject('useRouter')()
    const store = inject('useStore')()
    const name = ref(null)

    const { projectId } = route.params

    onMounted(() => {
      if(projectId){
        projectApi.getBasic({ projectId }).then(resp => {
          const data = resp.data
          name.value = data.name
          
          store.commit('setTitle', { title: name.value })
        })
      }
    })

    return {
      name,
      projectId,

      onClickBack(){
        router.back()
      },

      onSubmit(){
        if(projectId){
          // edit
          projectApi.updateBasic({
            projectId,
            name: name.value
          }).then(resp => {
            router.back()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }else{
          // create
          projectApi.create({
            name: name.value
          }).then(resp => {
            const projectId = resp.data
            router.back()
          }, resp => {
            qUtil.notifyError(resp.message)
          })
        }
        
      },
    }
  }
};
</script>