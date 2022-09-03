import { request } from 'common'
const API_AGENT_PREFIX = '/agent/api/user'

export default {
  list({ agentId = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AGENT_PREFIX}/agent/list?agentId=${encodeURIComponent(agentId)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  generateJoinScript({ osType, agentId, accessToken, serverHost, serverRpcPort, serverRestUseSsl, dataDir }){
    return request().post(`${API_AGENT_PREFIX}/agent/join/script/generate`, {
      osType,
      agentId,
      accessToken,
      serverHost,
      serverRpcPort,
      serverRestUseSsl,
      dataDir,
    })
  },
}
