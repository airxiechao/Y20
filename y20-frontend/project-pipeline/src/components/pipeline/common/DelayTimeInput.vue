<template>
  <div class="delay-time-input q-mt-sm row full-width">
    <div :class="{
      'col-12':true, 
      'col-sm-4': true, 
      'q-pr-md': $q.screen.gt.xs,
      'q-pb-md': $q.screen.lt.sm,
    }">
      <q-input outlined type="number" :min="0" label="小时" v-model="hour" />
    </div>
    <div :class="{
      'col-12':true, 
      'col-sm-4': true, 
      'q-pr-md': $q.screen.gt.xs,
      'q-pb-md': $q.screen.lt.sm,
    }">
      <q-input outlined type="number" :min="0" :max="59" label="分钟" v-model="minute" />
    </div>
    <div class="col-12 col-sm-4">
      <q-input outlined type="number" :min="0" :max="59" label="秒" v-model="second" />
    </div>
  </div>
</template>


<script>
import { ref, inject, computed, watch, onMounted } from 'vue'

export default {
  props: {
    modelValue: '',
  },
  setup(props, { emit }){
    const $q = inject('useQuasar')()

    const hour = ref(null)
    const minute = ref(null)
    const second = ref(null)

    const initValue = (secs) => {
      if(!secs){
        return
      }

      hour.value = parseInt(secs / 3600)
      minute.value = parseInt(secs % 3600 / 60)
      second.value = parseInt(secs % 60)
    }

    const updateValue = (h, m, s) => {
      const secs = (h || 0) * 3600 + (m || 0) * 60 + (s || 0)
      emit('update:modelValue', secs)
    }

    watch(() => props.modelValue, (newValue, oldValue) => {
      initValue(newValue)
    })

    watch(hour, (newValue, oldValue) => {
      updateValue(parseInt(newValue), minute.value, second.value)
    })

    watch(minute, (newValue, oldValue) => {
      updateValue(hour.value, parseInt(newValue), second.value)
    })

    watch(second, (newValue, oldValue) => {
      updateValue(hour.value, minute.value, parseInt(newValue))
    })

    onMounted(() => {
      initValue(props.modelValue)
    })

    return {
      $q,

      hour,
      minute,
      second,
    }
  }
}
</script>