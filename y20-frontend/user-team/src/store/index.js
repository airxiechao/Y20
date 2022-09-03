import { moduleUtil } from 'common'

import userTeamStore from '@/store/userTeam/userTeam.store'

const store = {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {},
}

export default moduleUtil.mergeModules(store, userTeamStore)