import workspaceTemplateMutations from './workspaceTemplate.mutations'
import workspaceTemplateActions from './workspaceTemplate.actions'

const workspaceTemplateStore = {
  state: {
    onlyMy: false,
  },
  mutations: workspaceTemplateMutations,
  actions: workspaceTemplateActions,
}

export default workspaceTemplateStore