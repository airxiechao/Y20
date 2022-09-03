import { createApp } from 'vue'
import Home from '@/views/Home'
import store from '@/store'
import { Quasar, Notify, Dialog, LoadingBar } from 'quasar'
import 'quasar/dist/quasar.prod.css'
import qZhCN from 'quasar/lang/zh-CN'
import '@quasar/extras/roboto-font/roboto-font.css'
import '@quasar/extras/material-icons/material-icons.css'
import { createI18n } from 'vue-i18n'

const i18n = createI18n({
  locale: 'zhCN',
})

const app = createApp(Home)

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
      primary: '#1E5EFF',
    },
    loadingBar: { 
      skipHijack: true,
      color: 'info',
      size: '4px',
    }
  },
})
app.use(i18n)

app.mount('#app')

