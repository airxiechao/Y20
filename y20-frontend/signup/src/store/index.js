import { moduleUtil } from 'common'

import signupStore from '@/store/signup/signup.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, signupStore)