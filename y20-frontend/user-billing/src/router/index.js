import UserBilling from '@/views/UserBilling'
import { PAGE_NAME } from '@/page.config'

import QuotaDetail from '@/components/user/billing/quota/QuotaDetail'
import QuotaAdd from '@/components/user/billing/quota/QuotaAdd'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: UserBilling,
    children: [
      {
        path: '',
        component: QuotaDetail,
      },
      {
        path: 'quota',
        component: QuotaDetail,
      },
      {
        path: 'quota/add',
        component: QuotaAdd,
      },
    ],
  }
]

export default routes