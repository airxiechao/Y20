import { moduleUtil } from 'common'

import projectFileStore from '@/store/projectFile/projectFile.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, projectFileStore)