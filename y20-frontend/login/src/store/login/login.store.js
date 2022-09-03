import loginMutations from './login.mutations'
import loginActions from './login.actions'

const loginStore = {
  state: {
    twoFactorToken: null,
  },
  mutations: loginMutations,
  actions: loginActions,
}

export default loginStore