import { moduleUtil } from 'common'

import userSessionStore from '@/store/userSession/userSession.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, userSessionStore)