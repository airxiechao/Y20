import { createStore } from "vuex"
import Cookies from 'js-cookie'
import projectApi from '@/api/project.api'

const store = window.store = createStore({
  state: {
    pathBeforeLogin: null,
  },
  getters: {
    username(){
      return Cookies.get('username')
    },
    accessToken(){
      return Cookies.get('accessToken')
    },
  },
  mutations: {
    setPathBeforeLogin(state, path){
      state.pathBeforeLogin = path
    },
    setLogin(state, { username, accessToken }){
      Cookies.set('username', username)
      Cookies.set('accessToken', accessToken)
    },
    clearLogin(state){
      Cookies.remove('username')
      Cookies.remove('accessToken')
      state.pathBeforeLogin = null
    },
    setTitle(state, { title }){
      if(title){
        document.title = title + ' - Y20'
      }else{
        document.title = '鲲擎流水线'
      }
    },
  },
  actions: {
    
  },
  modules: {
    workspace: {
      namespaced: true,
      state: {
        teamId: null
      },
      mutations: {
        setTeamId(state, { teamId  }){
          state.teamId = teamId
        },
      },
    },
    project: {
      namespaced: true,
      state: {
        id: null,
        name: null,
      },
      mutations: {
        setProjectId(state, { id }){
          state.id = id
        },
        setProjectName(state, { name }){
          state.name = name
        }
      },
      actions: {
        queryProjectName({ commit, state }){
          projectApi.get({ projectId: state.id }).then(resp => {
            const project = resp.data
            commit('setProjectName', {
              name: project.name,
            })
          })
        }
      },
    },
    user: {},
    home: {},
  },
})

export default store
