import { moduleUtil } from 'common'

import projectPipelineStore from '@/store/projectPipeline/projectPipeline.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, projectPipelineStore)