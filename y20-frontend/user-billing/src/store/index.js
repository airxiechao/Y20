import { moduleUtil } from 'common'

import userBillingStore from '@/store/userBilling/userBilling.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, userBillingStore)