import { moduleUtil } from 'common'

import workspaceProjectStore from '@/store/workspaceProject/workspaceProject.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, workspaceProjectStore)