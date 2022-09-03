import { moduleUtil } from 'common'

import userAccountStore from '@/store/userAccount/userAccount.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, userAccountStore)