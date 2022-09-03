import WorkspaceTemplate from '@/views/WorkspaceTemplate'
import { PAGE_NAME } from '@/page.config'

import TemplateList from '@/components/template/TemplateList'
import TemplateDetial from '@/components/template/TemplateDetial'
import TemplateApply from '@/components/template/TemplateApply'

import TemplateDescription from '@/components/template/description/TemplateDescription'
import TemplateStepList from '@/components/template/step/TemplateStepList'
import TemplateStep from '@/components/template/step/TemplateStep'
import TemplateVariableList from '@/components/template/variable/TemplateVariableList'
import TemplateVariableDetail from '@/components/template/variable/TemplateVariableDetail'
import TemplateFileList from '@/components/template/file/TemplateFileList'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: WorkspaceTemplate,
    children: [
      {
        path: '',
        component: TemplateList,
      },
      {
        path: 'list',
        component: TemplateList,
      },
      {
        path: ':templateId',
        component: TemplateDetial,
        children: [
          {
            path: 'description',
            alias: '',
            component: TemplateDescription,
          },
          {
            path: 'step',
            component: TemplateStepList,
          },
          {
            path: 'step/:stepPosition',
            component: TemplateStep,
          },
          {
            path: 'variable',
            component: TemplateVariableList,
          },
          {
            path: 'variable/:templateVariableName',
            component: TemplateVariableDetail,
          },
          {
            path: 'file',
            component: TemplateFileList,
          },
        ],
      },
      {
        path: ':templateId/apply',
        component: TemplateApply,
      },
    ],
  }
]

export default routes