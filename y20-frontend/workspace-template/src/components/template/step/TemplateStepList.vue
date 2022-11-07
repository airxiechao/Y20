<template>
  <div class="template-step-list">
    <div class="page-heading text-primary q-pb-md">步骤列表</div>
    <q-card flat>
      <q-list separator>
        <template v-if="!steps || steps.length == 0">
          <q-item class="q-py-md">
            <q-item-section avatar style="min-width:35px; padding-right:0;">
              <q-icon name="warning" />
            </q-item-section>
            <q-item-section>没有步骤</q-item-section>
          </q-item>
        </template>
        <template v-for="(step, i) in steps" :key="i">
          <q-item clickable class="q-py-md" :style="{
            //'border-left': `4px solid ${getGroupColor(i)}`
          }" @click="onClickStep(i)">
            <q-item-section avatar class="gt-sm">
            <q-icon :name="step.type == 'remote-prepare-env' ? 'computer' : 'account_tree'" color="black" size="34px" />
            </q-item-section>

            <q-item-section>
              <q-item-label :class="{
                'cursor-pointer': true,
                'text-grey': step.disabled,
              }" :style="{
                'text-decoration': step.disabled ? 'line-through' : 'unset',
              }">
                <span class="vertical-middle q-mr-sm">步骤 {{i+1}}</span>
                <span class="vertical-middle">{{ step.name }}</span>
              </q-item-label>
              <q-item-label caption>{{ step.typeName || step.type }}</q-item-label>
            </q-item-section>

            <q-item-section side>
              <div class="text-grey-8 q-gutter-xs">
                <q-toggle size="sm" :modelValue="!step.disabled" :icon="step.condition ? 'visibility' : undefined" />
              </div>
            </q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-card>
  </div>
</template>

<script>
import { ref, inject, computed, watch } from "vue"

export default {
  setup() {
    const $q = inject('useQuasar')()
    const route = inject("useRoute")()
    const router = inject("useRouter")()
    const { templateId } = route.params

    const template = inject('template')

    const steps = computed(() => {
      return template.value.steps || []
    })

    return {
      $q,
      steps,

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

      onClickStep(position) {
        router.push(`/workspace/template/${templateId}/step/${position}`)
      },
    };
  },
};
</script>

