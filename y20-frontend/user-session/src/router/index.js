import UserSession from '@/views/UserSession'
import { PAGE_NAME } from '@/page.config'

import SessionList from '@/components/user/session/SessionList'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: UserSession,
    children: [
      {
        path: '',
        component: SessionList,
      },
      {
        path: 'list',
        component: SessionList,
      },
    ],
  }
]

export default routes