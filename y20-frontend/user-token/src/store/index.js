import { moduleUtil } from 'common'

import userTokenStore from '@/store/userToken/userToken.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, userTokenStore)