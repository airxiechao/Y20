import { PAGE_NAME } from '@/page.config'
import Signup from '@/views/Signup'
import SignupForm from '@/components/SignupForm'
import SignupSuccess from '@/components/SignupSuccess'

const routes = [
  {
    path: PAGE_NAME,
    component: Signup,
    children: [
      {
        path: '',
        component: SignupForm,
      },
      {
        path: 'success',
        component: SignupSuccess,
      }
    ],
  }
]

export default routes