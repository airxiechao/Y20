import { computed, inject } from 'vue'
import artifactApi from '@/api/artifact.api'

export default {
  data(){
    
    const route = inject('useRoute')()
    const { projectId, pipelineId } = route.params

    const uploadPipelineStepFileUrl = computed(() => {
      return artifactApi.getUploadPipelineStepFileUrl()
    })
    const uploadPipelineStepFileHeaders = computed(() => {
      return artifactApi.getUploadPipelineStepFileHeaders({ projectId })
    })
    const uploadPipelineStepFileFields = computed(() => {
      return artifactApi.getUploadPipelineStepFileFields({ pipelineId })
    })

    return {
      uploadPipelineStepFileUrl,
      uploadPipelineStepFileHeaders,
      uploadPipelineStepFileFields,
    }
  }
}
