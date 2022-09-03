import { request } from 'common'
const API_PROJECT_PREFIX = '/project/api/user'

export default {
  listVariable(){
    return request().get(`${API_PROJECT_PREFIX}/project/variable/list`)
  },
  getVariable({ name }){
    return request().get(`${API_PROJECT_PREFIX}/project/variable/get?name=${name}`)
  },
  createVariable({ name, value, kind, paramType }){
    return request().post(`${API_PROJECT_PREFIX}/project/variable/create`, {
      variable: {
        name,
        value,
        kind,
        paramType,
      }
    })
  },
  updateVariable({ name, value, kind, paramType }){
    return request().post(`${API_PROJECT_PREFIX}/project/variable/update`, {
      variable: {
        name,
        value,
        kind,
        paramType,
      }
    })
  },
  deleteVariable({ variableName }){
    return request().post(`${API_PROJECT_PREFIX}/project/variable/delete`, {
      variableName,
    })
  },
}