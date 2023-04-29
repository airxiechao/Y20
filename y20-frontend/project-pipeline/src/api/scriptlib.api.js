import { request } from 'common'
const API_SCRIPTLIB_PREFIX = '/scriptlib/api/user'

export default {
  get({ isPublic = false, scriptPieceId }){
    return request().get(`${API_SCRIPTLIB_PREFIX}/script/piece/get?isPublic=${isPublic}&scriptPieceId=${encodeURIComponent(scriptPieceId)}`)
  },
  list({ isPublic = false, name = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_SCRIPTLIB_PREFIX}/script/piece/list?isPublic=${isPublic}&name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  delete({ scriptPieceId }){
    return request().post(`${API_SCRIPTLIB_PREFIX}/script/piece/delete`, {
      scriptPieceId,
    })
  },
  create({ name, script }){
    return request().post(`${API_SCRIPTLIB_PREFIX}/script/piece/create`, {
      name,
      script,
    })
  },
  update({ scriptPieceId, name, script }){
    return request().post(`${API_SCRIPTLIB_PREFIX}/script/piece/update`, {
      scriptPieceId,
      name,
      script,
    })
  },
}
