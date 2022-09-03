import UserAccount from '@/views/UserAccount'
import { PAGE_NAME } from '@/page.config'

import AccountDetial from '@/components/user/account/AccountDetail'
import UpdateAccountPassword from '@/components/user/account/UpdateAccountPassword'
import UpdateAccountMobile from '@/components/user/account/UpdateAccountMobile'
import UpdateAccountEmail from '@/components/user/account/UpdateAccountEmail'

import EnableAccountTwoFactor from '@/components/user/account/EnableAccountTwoFactor'
import DisableAccountTwoFactor from '@/components/user/account/DisableAccountTwoFactor'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: UserAccount,
    children: [
      {
        path: '',
        component: AccountDetial,
      },
      {
        path: 'detail',
        component: AccountDetial,
      },
      {
        path: 'password',
        component: UpdateAccountPassword,
      },
      {
        path: 'mobile',
        component: UpdateAccountMobile,
      },
      {
        path: 'email',
        component: UpdateAccountEmail,
      },
      {
        path: 'twofactor/enable',
        component: EnableAccountTwoFactor,
      },
      {
        path: 'twofactor/disable',
        component: DisableAccountTwoFactor,
      },
    ],
  }
]

export default routes