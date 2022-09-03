import { computed, inject } from 'vue'
import artifactApi from '@/api/artifact.api'

export default {
  data(){
    
    const route = inject('useRoute')()
    const { projectId, pipelineId } = route.params

    const uploadPipelineTempFileUrl = computed(() => {
      return artifactApi.getUploadPipelineTempFileUrl()
    })
    const uploadPipelineTempFileHeaders = computed(() => {
      return artifactApi.getUploadPipelineTempFileHeaders({ projectId })
    })
    const uploadPipelineTempFileFields = computed(() => {
      return artifactApi.getUploadPipelineTempFileFields({ pipelineId })
    })

    return {
      uploadPipelineTempFileUrl,
      uploadPipelineTempFileHeaders,
      uploadPipelineTempFileFields,
    }
  }
}
