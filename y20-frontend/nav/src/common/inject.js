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
          position: 'top-right',
          textColor: 'white',
          icon: 'error',
          message: text
        })
      },
      notifySuccess(text){
        $q.notify({
          color: 'green-5',
          position: 'top-right',
          textColor: 'white',
          icon: 'done',
          message: text
        })
      }
    }
  })
}

export default {
  install
}