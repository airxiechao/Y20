import ProjectMonitor from '@/views/ProjectMonitor'
import { PAGE_NAME } from '@/page.config'

import MonitorList from '@/components/monitor/MonitorList'
import MonitorSingle from '@/components/monitor/MonitorSingle'
import MonitorMetric from '@/components/monitor/MonitorMetric'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: ProjectMonitor,
    children: [
      {
        path: '',
        component: MonitorList,
      },
      {
        path: 'list',
        alias: '',
        component: MonitorList,
      },
      {
        path: 'create',
        component: MonitorSingle,
      },
      {
        path: ':monitorId',
        component: MonitorSingle,
      },
      {
        path: ':monitorId/metric',
        component: MonitorMetric,
      },
    ],
  }
]

export default routes