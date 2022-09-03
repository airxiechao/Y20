import WorkspaceProject from '@/views/WorkspaceProject'
import { PAGE_NAME } from '@/page.config'

import ProjectList from '@/components/project/ProjectList'
import SingleProject from '@/components/project/SingleProject'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: WorkspaceProject,
    children: [
      {
        path: '',
        component: ProjectList,
      }, 
      {
        path: 'list',
        component: ProjectList,
      }, 
      {
        path: 'create',
        component: SingleProject,
      },
      {
        path: ':projectId',
        component: SingleProject,
      },
    ],
  }
]

export default routes