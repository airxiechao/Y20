import routes from '@/router'
import store from '@/store'
import { PAGE_NAME } from '@/page.config'

window.Pages = window.Pages || {}
window.Pages[PAGE_NAME] = {
  name: PAGE_NAME,
  routes,
  store,
}