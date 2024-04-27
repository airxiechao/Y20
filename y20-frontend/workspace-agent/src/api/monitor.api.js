import { request } from 'common'
const API_PROJECT_PREFIX = '/monitor/api/user'

export default {
  listAgentMetric({ agentId }){
    return request().get(`${API_PROJECT_PREFIX}/monitor/agent/metric/list?agentId=${encodeURIComponent(agentId)}`)
  },
}