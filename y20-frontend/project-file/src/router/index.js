import ProjectFile from '@/views/ProjectFile'
import { PAGE_NAME } from '@/page.config'

import ListProjectFile from '@/components/file/ListProjectFile'
import UploadProjectFile from '@/components/file/UploadProjectFile'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: ProjectFile,
    children: [
      {
        path: '',
        component: ListProjectFile,
      },
      {
        path: 'list',
        component: ListProjectFile,
      },
      {
        path: 'upload',
        component: UploadProjectFile,
      },
    ],
  }
]

export default routes