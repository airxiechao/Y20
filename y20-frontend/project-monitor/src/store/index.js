import { moduleUtil } from 'common'

import projectMonitorStore from '@/store/projectMonitor/projectMonitor.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, projectMonitorStore)