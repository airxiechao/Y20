import workspaceAgentMutations from './workspaceAgent.mutations'
import workspaceAgentActions from './workspaceAgent.actions'

const workspaceAgentStore = {
  state: {
    listAgent: {
      pagination: {
        sortBy: 'agentId',
        descending: false,
        page: 1,
        rowsPerPage: 8,
        rowsNumber: 0
      }
    }
  },
  mutations: workspaceAgentMutations,
  actions: workspaceAgentActions,
}

export default workspaceAgentStore