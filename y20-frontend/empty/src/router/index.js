import App from '@/views/App'
import { PAGE_NAME } from '@/page.config'

const routes = [
  {
    path: PAGE_NAME,
    component: App,
    children: [
      
    ],
  }
]

export default routes