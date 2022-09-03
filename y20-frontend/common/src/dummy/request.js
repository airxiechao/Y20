import axios from 'axios'

const request = window.request = axios.create({

})

request.interceptors.request.use((config) => {
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
  return Promise.reject(error)
})

function install(app, options){
  app.config.globalProperties.$request = request
  app.provide('request', app.config.globalProperties.$request)
}

export default {
  request,
  install
}
