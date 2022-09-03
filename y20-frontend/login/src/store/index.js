import { moduleUtil } from 'common'

import loginStore from '@/store/login/login.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, loginStore)