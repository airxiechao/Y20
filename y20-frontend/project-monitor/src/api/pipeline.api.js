import axios from 'axios';
import { request } from 'common'
import { store } from 'common'

const API_PIPELINE_PREFIX = '/pipeline/api/user'
const CancelToken = axios.CancelToken;

export default {
  list({ projectId = '', name = '', orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/list?${projectId?'projectId='+projectId+'&':''}name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  listVariable({ projectId, pipelineId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/variable/list?${projectId?'projectId='+projectId+'&':''}pipelineId=${pipelineId}`)
  },
}
