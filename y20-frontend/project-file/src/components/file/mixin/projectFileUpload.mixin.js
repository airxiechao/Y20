import { ref, computed, inject } from 'vue'
import artifactApi from '@/api/artifact.api'

export default {
  data(){

    const dir = ref('')
    
    const route = inject('useRoute')()
    const { projectId } = route.params

    const uploadProjectFileUrl = computed(() => {
      return artifactApi.getUploadProjectFileUrl()
    })
    const uploadProjectFileHeaders = computed(() => {
      return artifactApi.getUploadProjectFileHeaders({ projectId })
    })
    const uploadProjectFileFields = computed(() => {
      return artifactApi.getUploadProjectFileFields({ dir: dir.value })
    })

    return {
      dir,
      uploadProjectFileUrl,
      uploadProjectFileHeaders,
      uploadProjectFileFields,
    }
  }
}