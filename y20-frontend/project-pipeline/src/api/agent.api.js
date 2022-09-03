import { request } from 'common'
const API_AGENT_PREFIX = '/agent/api/user'

export default {

  listAgent({ agentId }){
    return request().get(`${API_AGENT_PREFIX}/agent/list?agentId=${encodeURIComponent(agentId)}`)
  },
}
