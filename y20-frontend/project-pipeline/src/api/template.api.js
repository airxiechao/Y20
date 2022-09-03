import { request } from 'common'
const API_TEMPLATE_PREFIX = '/template/api/user'

export default {
  listMy({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_TEMPLATE_PREFIX}/template/list/my?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
}
