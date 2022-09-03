import UserTeam from '@/views/UserTeam'
import { PAGE_NAME } from '@/page.config'

import TeamDetail from '@/components/user/team/TeamDetail'
import TeamJoin from '@/components/user/team/TeamJoin'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: UserTeam,
    children: [
      {
        path: '',
        component: TeamDetail,
      },
      {
        path: 'detail',
        component: TeamDetail,
      },
      {
        path: 'join',
        component: TeamJoin,
      },
    ],
  }
]

export default routes