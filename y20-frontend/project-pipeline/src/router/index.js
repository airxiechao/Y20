import ProjectPipeline from '@/views/ProjectPipeline'
import { PAGE_NAME } from '@/page.config'

import PipelineList from '@/components/pipeline/PipelineList'
import PipelineCreate from '@/components/pipeline/PipelineCreate'
import PipelineSingle from '@/components/pipeline/PipelineSingle'

import PipelineDetail from '@/components/pipeline/PipelineDetail'
import PipelineRunList from '@/components/pipeline/run/PipelineRunList'
import PipelineStepList from '@/components/pipeline/step/PipelineStepList'
import PipelineStepAdd from '@/components/pipeline/step/PipelineStepAdd'
import PipelineStepEdit from '@/components/pipeline/step/PipelineStepEdit'
import PipelineStepNew from '@/components/pipeline/step/PipelineStepNew'
import StepTypeList from '@/components/pipeline/step/StepTypeList'

import PipelinePublish from '@/components/pipeline/template/PipelinePublish'

import PipelineRunDetailStep from '@/components/pipeline/run/PipelineRunDetailStep'
import PipelineRunDetailFile from '@/components/pipeline/run/PipelineRunDetailFile'
import PipelineRunCreate from '@/components/pipeline/run/PipelineRunCreate'

import PipelineVariableList from '@/components/pipeline/variable/PipelineVariableList'
import PipelineVariableDetail from '@/components/pipeline/variable/PipelineVariableDetail'

import PipelineFileList from '@/components/pipeline/file/PipelineFileList'
import PipelineFileUpload from '@/components/pipeline/file/PipelineFileUpload'

import PipelineWebhookTriggerList from '@/components/pipeline/trigger/PipelineWebhookTriggerList'
import PipelineWebhookTriggerDetail from '@/components/pipeline/trigger/PipelineWebhookTriggerDetail'

import PipelinePendingList from '@/components/pipeline/pending/PipelinePendingList'
import PipelinePendingDetail from '@/components/pipeline/pending/PipelinePendingDetail'

import PipelineConfig from '@/components/pipeline/config/PipelineConfig'

const routes = [
  {
    path: PAGE_NAME.split('-')[1],
    component: ProjectPipeline,
    children: [
      {
        path: '',
        component: PipelineList,
      },
      {
        path: 'list',
        component: PipelineList,
      },
      {
        path: 'create',
        component: PipelineCreate,
      }, 
      {
        path: ':pipelineId',
        component: PipelineSingle,
        children: [
          {
            path: 'detail',
            alias: '',
            component: PipelineDetail,
            children: [
              {
                path: 'run',
                alias: '',
                component: PipelineRunList,
              },
              {
                path: 'step',
                component: PipelineStepList,
              }, 
              {
                path: 'variable',
                component: PipelineVariableList,
              },
              {
                path: 'variable/create',
                component: PipelineVariableDetail,
              },
              {
                path: 'variable/:pipelineVariableName',
                component: PipelineVariableDetail,
              },
              {
                path: 'file',
                component: PipelineFileList,
              },
              {
                path: 'file/upload',
                component: PipelineFileUpload,
              },
              {
                path: 'trigger',
                component: PipelineWebhookTriggerList,
              },
              {
                path: 'trigger/create',
                component: PipelineWebhookTriggerDetail,
              },
              {
                path: 'trigger/:pipelineWebhookTriggerId',
                component: PipelineWebhookTriggerDetail,
              },
              {
                path: 'pending',
                component: PipelinePendingList,
              },
              {
                path: 'pending/:pipelinePendingId',
                component: PipelinePendingDetail,
              },
              {
                path: 'config',
                component: PipelineConfig,
              },
            ],
          }, 
          {
            path: 'publish',
            component: PipelinePublish,
          },
          {
            path: 'run/:pipelineRunId/step',
            alias: 'run/:pipelineRunId',
            component: PipelineRunDetailStep,
          },
          {
            path: 'run/:pipelineRunId/file',
            component: PipelineRunDetailFile,
          },
          {
            path: 'run/create',
            component: PipelineRunCreate,
          },
          {
            path: 'step/:stepPosition',
            component: PipelineStepEdit,
          },
          {
            path: 'step/:stepPosition/add',
            component: PipelineStepAdd,
            children: [
              {
                path: 'list',
                alias: '',
                component: StepTypeList
              },
              {
                path: ':stepType',
                component: PipelineStepNew
              },
            ],
          }, 
        ],
      },
    ],
  }
]

export default routes