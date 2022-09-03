import { PAGE_NAME } from '@/page.config'
import Login from '@/views/Login'
import LoginTabs from '@/components/LoginTabs'
import LoginByTwoFactor from '@/components/LoginByTwoFactor'

const routes = [
  {
    path: PAGE_NAME,
    component: Login,
    children: [
      {
        path: '',
        component: LoginTabs,
      },
      {
        path: 'twofactor',
        component: LoginByTwoFactor,
      },
    ],
  }
]

export default routes