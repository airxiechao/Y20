import Cookies from 'js-cookie'
import homeMutations from './home.mutations'
import homeActions from './home.actions'

const homeStore = {
  namespaced: true,
  state: {},
  getters: {
    accessToken(){
      return Cookies.get('accessToken')
    },
  },
  mutations: {},
  actions: {},
  mutations: homeMutations,
  actions: homeActions,
}

export default homeStore