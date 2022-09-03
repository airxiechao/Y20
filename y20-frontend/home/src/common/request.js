import axios from 'axios'

const request = window.request = axios.create({

})

request.interceptors.request.use((config) => {
  
  config.headers['Content-Type'] = 'application/json; charset=UTF-8'

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
  
  return Promise.reject(resp)
})

export default request
