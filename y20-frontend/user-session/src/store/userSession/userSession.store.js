import userSessionMutations from './userSession.mutations'
import userSessionActions from './userSession.actions'

const userSessionStore = {
  state: {
  },
  mutations: userSessionMutations,
  actions: userSessionActions,
}

export default userSessionStore