import { ref, computed, inject } from 'vue'
import artifactApi from '@/api/artifact.api'

export default {
  data(){
    
    const route = inject('useRoute')()
    const { projectId, pipelineId } = route.params

    const dir = ref('')

    const uploadPipelineFileUrl = computed(() => {
      return artifactApi.getUploadPipelineFileUrl()
    })
    const uploadPipelineFileHeaders = computed(() => {
      return artifactApi.getUploadPipelineFileHeaders({ projectId })
    })
    const uploadPipelineFileFields = computed(() => {
      return artifactApi.getUploadPipelineFileFields({ 
        pipelineId, 
        dir: dir.value,
      })
    })

    return {
      dir,
      uploadPipelineFileUrl,
      uploadPipelineFileHeaders,
      uploadPipelineFileFields,
    }
  }
}
