import { request } from 'common'
import { store } from 'common'
const API_PIPELINE_PREFIX = '/pipeline/api/user'

export default {
  listStepType(){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/type/list`)
  },
  getStepType({ type }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/type/get?type=${type}`)
  },
}
