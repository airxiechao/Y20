import UserToken from '@/views/UserToken'
import { PAGE_NAME } from '@/page.config'

import TokenList from '@/components/user/token/TokenList'
import TokenCreate from '@/components/user/token/TokenCreate'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: UserToken,
    children: [
      {
        path: '',
        component: TokenList,
      },
      {
        path: 'list',
        component: TokenList,
      },
      {
        path: 'create',
        component: TokenCreate,
      },
    ],
  }
]

export default routes