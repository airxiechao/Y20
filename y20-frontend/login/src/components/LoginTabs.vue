<template>
  <div>
    <q-tabs
      v-model="tab"
      dense
      class="text-grey"
      active-color="primary"
      indicator-color="primary"
      align="justify"
      narrow-indicator
    >
      <q-tab name="byUsername" label="通过用户名" />
      <q-tab name="byMobile" label="通过手机号" />
    </q-tabs>
    <q-tab-panels v-model="tab">
      <q-tab-panel name="byUsername" class="tab">
        <LoginByUsername />
      </q-tab-panel>

      <q-tab-panel name="byMobile" class="tab">
        <LoginByMobile />
      </q-tab-panel>
    
    </q-tab-panels>
  </div>
</template>


<script>
import LoginByUsername from '@/components/LoginByUsername'
import LoginByMobile from '@/components/LoginByMobile'

import { ref, inject, onMounted } from 'vue'

export default {
  components: {
    LoginByUsername,
    LoginByMobile,
  },
  setup () {

    return {
      tab: ref('byUsername'),
    }
  }
};
</script>