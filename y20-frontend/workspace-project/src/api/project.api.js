import { request } from 'common'
const API_PROJECT_PREFIX = '/project/api/user'

export default {
  listDetail({ name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_PROJECT_PREFIX}/project/detail/list?name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  getBasic({ projectId }){
    return request().get(`${API_PROJECT_PREFIX}/project/basic/get?projectId=${projectId}`)
  },
  create({ name }){
    return request().post(`${API_PROJECT_PREFIX}/project/create`, {
      name,
    })
  },
  delete({ projectId }){
    return request().post(`${API_PROJECT_PREFIX}/project/delete`, {
      projectId,
    })
  },
  updateBasic({ projectId, name }){
    return request().post(`${API_PROJECT_PREFIX}/project/basic/update`, {
      projectId,
      name,
    })
  },
  updateBookmark({ projectId, bookmarked }){
    return request().post(`${API_PROJECT_PREFIX}/project/bookmark/update`, {
      projectId,
      bookmarked,
    })
  },
}
