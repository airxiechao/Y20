import { moduleUtil } from 'common'

import emptyStore from '@/store/empty/empty.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, emptyStore)