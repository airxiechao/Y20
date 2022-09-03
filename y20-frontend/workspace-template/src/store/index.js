import { moduleUtil } from 'common'

import workspaceTemplateStore from '@/store/workspaceTemplate/workspaceTemplate.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, workspaceTemplateStore)