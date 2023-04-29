import { createApp } from 'vue'
import Nav from '@/views/Nav'
import router from '@/router'
import store from '@/store'
import eventBus from '@/common/eventBus'
import request from '@/common/request'
import inject from '@/common/inject'
import { Quasar, Notify, Dialog, LoadingBar } from 'quasar'
import 'quasar/dist/quasar.prod.css'
import qZhCN from 'quasar/lang/zh-CN'
import '@quasar/extras/roboto-font/roboto-font.css'
import '@quasar/extras/material-icons/material-icons.css'
import { createI18n } from 'vue-i18n'

import 'common/dist/common.css'

const i18n = createI18n({
  locale: 'zhCN',
})

const app = createApp(Nav)

app.use(router)
app.use(store)
app.use(Quasar, {
  lang: qZhCN,
  plugins: {
    Notify,
    Dialog,
    LoadingBar,
  },
  config: {
    brand: {
      primary: '#0d72dc',
    },
    loadingBar: { 
      skipHijack: true,
      color: 'info',
      size: '4px',
    }
  },
})
app.use(inject)
app.use(eventBus)
app.use(request)
app.use(i18n)

app.mount('#app')
