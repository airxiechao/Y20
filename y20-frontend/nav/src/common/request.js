import axios from 'axios'
import store from '@/store'
import router from '@/router'

const request = window.request = axios.create({

})

request.interceptors.request.use((config) => {
  
  config.headers['Content-Type'] = 'application/json; charset=UTF-8'

  // inject header: accessToken
  if(store.getters.accessToken){
    config.headers.auth = store.getters.accessToken
  }
  // inject header: projectId
  if(store.state.project.id) {
    config.headers.projectId = store.state.project.id
  }
  // inject header: teamId
  if(store.state.workspace.teamId) {
    config.headers.teamId = store.state.workspace.teamId
  }

  return config
}, (error) => {
  return Promise.reject(error)
})

request.interceptors.response.use((response) => {
  let resp = response.data
  if (resp.code != '0') {
    return Promise.reject(resp)
  }

  return resp
}, (error) => {
  let resp = (error.response && error.response.data) || {
    code: '-1',
    message: 'network error'
  }
  
  if(resp.code == '-2'){
    router.push("/login")
  }
  
  return Promise.reject(resp)
})

function install(app, options){
  app.config.globalProperties.$request = request
  app.provide('request', app.config.globalProperties.$request)
}

export default {
  request,
  install
}
