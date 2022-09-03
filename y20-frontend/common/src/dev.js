import { createApp } from 'vue'
import App from '@/views/App.vue'
import { Quasar } from 'quasar'
import 'quasar/dist/quasar.prod.css'
import '@quasar/extras/roboto-font/roboto-font.css'
import '@quasar/extras/material-icons/material-icons.css'
import { createI18n } from 'vue-i18n'
import request from '@/dummy/request'

const i18n = createI18n({
  locale: 'zhCN',
})

const app = createApp(App)

app.use(Quasar)
app.use(i18n)
app.use(request)

app.mount('#app')
