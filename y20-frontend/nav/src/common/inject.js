import { useStore } from 'vuex'
import { useRoute, useRouter }  from 'vue-router'
import { useQuasar } from 'quasar'

function install(app, options){
  app.provide('useStore', useStore)
  app.provide('useRoute', useRoute)
  app.provide('useRouter', useRouter)
  app.provide('useQuasar', useQuasar)
  app.provide('quasarUtil', function($q){
    return {
      notifyError(text){
        $q.notify({
          color: 'red-5',
          textColor: 'white',
          icon: 'warning',
          message: text
        })
      },
      notifySuccess(text){
        $q.notify({
          color: 'green-5',
          textColor: 'white',
          icon: 'cloud_done',
          message: text
        })
      }
    }
  })
}

export default {
  install
}