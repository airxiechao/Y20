<template>
  <div class="pipeline-step-list">
    <div class="row q-pb-md">
      <div class="col page-heading text-primary self-center">步骤列表</div>
      <div class="self-center">
        <q-btn flat class="bg-blue-1" icon="add" color="primary" label="添加步骤" @click="onClickAddStep(steps.length)" />
      </div>
    </div>
    <q-card flat>
      <q-list separator class="relative-position">
        <template v-if="loading">
          <q-item class="q-pa-md" v-for="si in [1]" :key="si">
            <q-item-section avatar>
              <q-skeleton type="QAvatar" animation="fade" size="38px" />
            </q-item-section>

            <q-item-section>
              <q-item-label>
                <q-skeleton type="text" animation="fade" />
              </q-item-label>
              <q-item-label caption>
                <q-skeleton type="text" animation="fade" style="width: 25%;" />
              </q-item-label>
            </q-item-section>
          </q-item>
        </template>
        <template v-else-if="!steps || steps.length == 0">
          <q-item class="q-py-md">
            <q-item-section avatar style="min-width:35px; padding-right:0;">
              <q-icon name="warning" />
            </q-item-section>
            <q-item-section>
              <div>
                <span class="vertical-middle">没有步骤？</span>
                <q-btn class="vertical-middle" color="primary" dense flat label="添加一个步骤" @click="onClickAddStep(steps.length)" />
                <span class="vertical-middle">。第一个步骤的类型应该是“准备环境”或“调用流水线”</span>
              </div>
            </q-item-section>
          </q-item>
        </template>
        <template v-else v-for="(step, i) in steps" :key="i">
          <q-item clickable class="q-py-md" :style="{
            //'border-left': `4px solid ${getGroupColor(i)}`
          }" @click="onClickStep(i)">
            <q-item-section avatar class="gt-xs">
              <q-icon 
                :name="step.type == 'remote-prepare-env' ? 'computer' : 'account_tree'" 
                :color="step.disabled ? 'grey' : 'black'"
                size="34px" 
              />
            </q-item-section>

            <q-item-section>
              <q-item-label :class="{
                'cursor-pointer': true,
                'text-grey': step.disabled,
              }" :style="{
                'text-decoration': step.disabled ? 'line-through' : 'unset',
              }">
                <span class="q-mr-sm vertical-middle">步骤 {{i+1}}</span>
                <span class="vertical-middle">{{ step.name }}</span>
              </q-item-label>
              <q-item-label caption>
                {{ step.typeName }}
              </q-item-label>
            </q-item-section>

            <q-item-section top side>
              <div class="text-grey-8 q-gutter-xs">
                <q-toggle size="sm" :modelValue="!step.disabled" :icon="step.condition ? 'visibility' : undefined" @update:modelValue="onToggleStepDisabled(i, !$event)" />
                <q-btn flat dense round icon="more_vert" @click.stop>
                  <q-menu>
                    <q-list style="min-width: 100px">
                      <q-item clickable v-close-popup @click="onClickStep(i)">
                        <q-item-section>编辑</q-item-section>
                      </q-item>
                      <q-item clickable v-close-popup @click="onClickStepCondition(i)">
                        <q-item-section>设置执行条件</q-item-section>
                      </q-item>
                      <q-separator />
                      <q-item clickable v-close-popup @click="onClickAddStep(i)">
                        <q-item-section>添加步骤到之前</q-item-section>
                      </q-item>
                      <q-item clickable v-close-popup @click="onClickAddStep(i+1)">
                        <q-item-section>添加步骤到之后</q-item-section>
                      </q-item>
                      <q-separator />
                      <q-item clickable v-close-popup @click="onClickCopyStep(i, i)">
                        <q-item-section>复制步骤到之前</q-item-section>
                      </q-item>
                      <q-item clickable v-close-popup @click="onClickCopyStep(i, i+1)">
                        <q-item-section>复制步骤到之后</q-item-section>
                      </q-item>
                      <q-separator />
                      <q-item clickable v-close-popup @click="onClickCopyStepTo(i)">
                        <q-item-section>复制到...</q-item-section>
                      </q-item>
                      <q-item clickable v-close-popup @click="onClickMoveStepTo(i)">
                        <q-item-section>移动到...</q-item-section>
                      </q-item>
                      <q-separator />
                      <q-item clickable v-close-popup>
                        <q-item-section class="text-negative" @click="onClickDeleteStep(i)">删除</q-item-section>
                      </q-item>
                    </q-list>
                  </q-menu>
                </q-btn>
              </div>
            </q-item-section>
          </q-item>
        </template>

        <!-- <q-inner-loading :showing="loading">
          <q-spinner-gears size="30px" color="primary" />
        </q-inner-loading> -->
      </q-list>
    </q-card>
  </div>
</template>

<script>
import { ref, inject, computed, watch, onMounted } from "vue";
import pipelineApi from "@/api/pipeline.api";

export default {
  setup() {
    const $q = inject('useQuasar')()
    const qUtil = inject('quasarUtil')($q)
    const route = inject("useRoute")()
    const router = inject("useRouter")()
    const { projectId, pipelineId } = route.params
    const loading = ref(null)
    const steps = ref(null)
    const condition = ref(null)

    const listSteps = (noLoading) => {
      if(!noLoading){
        loading.value = true
      }
      pipelineApi.listStep({ pipelineId }).then((resp) => {
        steps.value = resp.data || []
        loading.value = false
      });
    }

    onMounted(listSteps);

    return {
      loading,
      steps,
      condition,

      getGroupColor(position){
        const colors = ['#9c27b0', '#fdd835', '#8bc34a', '#2196f3', '#ff9800']
        let ci = 0
        for(let si = 0; si < steps.value.length; ++si){
          const step = steps.value[si]
          if(step.type == 'remote-prepare-env'){
            ci += 1
          }

          if(si == position){
            return colors[ci % colors.length]
          }
        }

        return colors[0]
      },

      onToggleStepDisabled(position, newValue){
        pipelineApi.disableStep({ 
          pipelineId, 
          position, 
          disabled: newValue
        }).then(resp => {
          steps.value[position].disabled = newValue
          qUtil.notifySuccess(`${newValue?'关闭':'打开'}步骤 ${position+1} 完成`)
        }, resp => {
          qUtil.notifyError(`${newValue?'关闭':'打开'}步骤 ${position+1} 发生错误`)
        })
      },

      onClickAddStep(position) {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step/${position}/add`)
      },
      onClickStep(position) {
        router.push(`/project/${projectId}/pipeline/${pipelineId}/step/${position}`)
      },
      onClickStepCondition(position) {
        condition.value = steps.value[position].condition
        $q.dialog({
          title: '设置执行条件',
          prompt: {
            model: condition,
            type: 'text',
            placeholder: '变量名',
            hint: '请输入变量名，仅当该变量的值不为空时执行步骤',
            outlined: true,
          },
          cancel: true,
        }).onOk(val => {
          pipelineApi.updateStepCondition({ pipelineId, position, condition:val }).then(resp => {
            steps.value[position].condition = val
            qUtil.notifySuccess(`更新执行条件完成`)
          }, resp => {
            qUtil.notifyError(`更新执行条件发生错误`)
          })
        })
      },
      onClickCopyStep(srcPosition, destPosition) {
        pipelineApi.copyStep({ pipelineId, srcPosition, destPosition }).then(resp => {
          listSteps(true)
          qUtil.notifySuccess(`复制步骤完成`)
        }, resp => {
          qUtil.notifyError(`复制步骤发生错误`)
        })
      },
      onClickCopyStepTo(srcPosition){
        $q.dialog({
          title: '复制到',
          prompt: {
            model: srcPosition+1,
            type: 'number',
            prefix: '第',
            suffix : '步',
            outlined: true,
            isValid(val){
              return val > 0 && val <= steps.value.length + 1
            },
          },
          cancel: true,
        }).onOk(val => {
          pipelineApi.copyStep({ pipelineId, srcPosition, destPosition:val-1 }).then(resp => {
            listSteps(true)
            qUtil.notifySuccess(`复制步骤完成`)
          }, resp => {
            qUtil.notifyError(`复制步骤发生错误`)
          })
        })
      },
      onClickMoveStepTo(srcPosition){
        $q.dialog({
          title: '移动到',
          prompt: {
            model: srcPosition+1,
            type: 'number',
            prefix: '第',
            suffix : '步',
            outlined: true,
            isValid(val){
              return val > 0 && val <= steps.value.length
            },
          },
          cancel: true,
        }).onOk(val => {
          pipelineApi.moveStep({ pipelineId, srcPosition, destPosition:val-1 }).then(resp => {
            listSteps(true)
            qUtil.notifySuccess(`移动步骤完成`)
          }, resp => {
            qUtil.notifyError(`移动步骤发生错误`)
          })
        })
      },
      onClickDeleteStep(position) {
        $q.dialog({
          title: '删除',
          message: `确定删除步骤 ${position+1} ？`,
          style: 'word-break: break-all;',
          cancel: true,
        }).onOk(data => {
          pipelineApi.deleteStep({ pipelineId, position }).then(resp => {
            listSteps(true)
            qUtil.notifySuccess(`删除步骤完成`)
          }, resp => {
            qUtil.notifyError(`删除步骤发生错误`)
          })
        })
        
      },
    };
  },
};
</script>

