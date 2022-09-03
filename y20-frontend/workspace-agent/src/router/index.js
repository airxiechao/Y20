import WorkspaceAgent from '@/views/WorkspaceAgent'
import { PAGE_NAME } from '@/page.config'

import ListAgent from '@/components/agent/ListAgent'
import ConfigAgentForm from '@/components/agent/ConfigAgentForm'
import JoinAgent from '@/components/agent/join/JoinAgent'
import JoinAgentScript from '@/components/agent/join/JoinAgentScript'
import JoinAgentManual from '@/components/agent/join/JoinAgentManual'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: WorkspaceAgent,
    children: [
      {
        path: '',
        component: ListAgent,
      },
      {
        path: 'list',
        component: ListAgent,
      },
      {
        path: ':agentId/config',
        component: ConfigAgentForm,
      },
      {
        path: 'join',
        component: JoinAgent,
        children: [
          {
            path: 'script',
            alias: '',
            component: JoinAgentScript,
          },
          {
            path: 'manual',
            component: JoinAgentManual,
          }
        ],
      },
    ],
  }
]

export default routes