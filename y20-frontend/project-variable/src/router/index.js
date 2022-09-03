import ProjectVariable from '@/views/ProjectVariable'
import { PAGE_NAME } from '@/page.config'

import ListProjectVariable from '@/components/variable/ListProjectVariable'
import SingleProjectVariable from '@/components/variable/SingleProjectVariable'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: ProjectVariable,
    children: [
      {
        path: '',
        component: ListProjectVariable,
      },
      {
        path: 'list',
        alias: '',
        component: ListProjectVariable,
      },
      {
        path: 'create',
        component: SingleProjectVariable,
      },
      {
        path: ':projectVariableName',
        component: SingleProjectVariable,
      },
    ],
  }
]

export default routes