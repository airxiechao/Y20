<template>
  <div>
    <template v-if="ENABLE_LOGIN_MOBILE" >
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
    </template>
    <div class="q-pt-sm" v-else>
      <LoginByUsername/>
    </div>
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
      ENABLE_LOGIN_MOBILE: ENABLE_LOGIN_MOBILE,
      tab: ref('byUsername'),
    }
  }
};
</script>