import projectPipelineMutations from './projectPipeline.mutations'
import projectPipelineActions from './projectPipeline.actions'

const projectPipelineStore = {
  state: {
    pipeline: null,
  },
  mutations: projectPipelineMutations,
  actions: projectPipelineActions,
}

export default projectPipelineStore