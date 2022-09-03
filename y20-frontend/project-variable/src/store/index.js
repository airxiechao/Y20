import { moduleUtil } from 'common'

import projectVariableStore from '@/store/projectVariable/projectVariable.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, projectVariableStore)