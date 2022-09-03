import { request } from 'common'
const API_AGENT_PREFIX = '/agent/api/user'

export default {
  list({ agentId = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AGENT_PREFIX}/agent/list?agentId=${encodeURIComponent(agentId)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  listDetail({ agentId = '', pageNo = '', pageSize = '', orderField = '', orderType = '' }){
    return request().get(`${API_AGENT_PREFIX}/agent/detail/list?agentId=${encodeURIComponent(agentId)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  getLatestVersion(){
    return request().get(`${API_AGENT_PREFIX}/agent/version/latest/get`)
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
  readConfig({ agentId }){
    return request().get(`${API_AGENT_PREFIX}/agent/config/read?agentId=${encodeURIComponent(agentId)}`)
  },
  saveConfig({ agentId, config }){
    return request().post(`${API_AGENT_PREFIX}/agent/config/save`, {
      agentId,
      config
    })
  },
  restart({ agentId }){
    return request().post(`${API_AGENT_PREFIX}/agent/restart`, {
      agentId,
    })
  },
  upgrade({ agentId }){
    return request().post(`${API_AGENT_PREFIX}/agent/upgrade`, {
      agentId,
    })
  },
  uninstall({ agentId }){
    return request().post(`${API_AGENT_PREFIX}/agent/uninstall`, {
      agentId,
    })
  },
  clean({ agentId }){
    return request().post(`${API_AGENT_PREFIX}/agent/clean`, {
      agentId,
    })
  },
}
