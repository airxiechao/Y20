import { moduleUtil } from 'common'

import workspaceAgentStore from '@/store/workspaceAgent/workspaceAgent.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, workspaceAgentStore)