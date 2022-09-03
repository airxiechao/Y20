import axios from 'axios';
import { request } from 'common'
import { store } from 'common'

const API_PIPELINE_PREFIX = '/pipeline/api/user'
const CancelToken = axios.CancelToken;

export default {
  list({ projectId = '', name = '', orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/list?${projectId?'projectId='+projectId+'&':''}name=${encodeURIComponent(name)}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  create({ name, flagOneRun }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/create`, {
      name,
      flagOneRun,
    })
  },
  publish({ name, projectId, pipelineId, templateId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/publish`, {
      name,
      projectId,
      pipelineId,
      templateId,
    })
  },
  copy({ pipelineId, toProjectId, toPipelineName }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/copy`, {
      pipelineId,
      toProjectId,
      toPipelineName,
    })
  },
  getBasic({ pipelineId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/basic/get?pipelineId=${pipelineId}`)
  },
  updateBasic({ pipelineId, name, description, flagOneRun, flagInjectProjectVariable, flagCronEnabled, cronBeginTime, cronIntervalSecs, cronInParams }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/basic/update`, {
      pipelineId,
      name,
      description,
      flagOneRun,
      flagInjectProjectVariable,
      flagCronEnabled,
      cronBeginTime,
      cronIntervalSecs,
      cronInParams,
    })
  },
  updateBookmark({ pipelineId, bookmarked }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/bookmark/update`, {
      pipelineId,
      bookmarked,
    })
  },
  delete({ pipelineId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/delete`, {
      pipelineId,
    })
  },

  // step

  listStep({ pipelineId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/list?pipelineId=${pipelineId}`)
  },
  getStep({ pipelineId, position }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/get?pipelineId=${pipelineId}&position=${position}`)
  },
  addStep({ pipelineId, type, name, params, position }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/add`, {
      pipelineId,
      step : {
        type,
        name,
        params,
      },
      position,
    })
  },
  disableStep({ pipelineId, position, disabled }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/disable`, {
      pipelineId,
      position,
      disabled,
    })
  },
  copyStep({ pipelineId, srcPosition, destPosition }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/copy`, {
      pipelineId,
      srcPosition,
      destPosition,
    })
  },
  moveStep({ pipelineId, srcPosition, destPosition }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/move`, {
      pipelineId,
      srcPosition,
      destPosition,
    })
  },
  updateStepCondition({ pipelineId, position, condition }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/condition/update`, {
      pipelineId,
      position,
      condition,
    })
  },
  updateStep({ pipelineId, position, step }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/update`, {
      pipelineId,
      position,
      step,
    })
  },
  deleteStep({ pipelineId, position }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/step/delete`, {
      pipelineId,
      position,
    })
  },
  listStepType(){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/type/list`)
  },
  getStepType({ type }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/step/type/get?type=${type}`)
  },

  // run

  listRun({ pipelineId, orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/run/list?pipelineId=${pipelineId}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },
  getRun({ pipelineId, pipelineRunId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/run/get?pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}`)
  },
  createRun({ pipelineId, name, inParams, flagDebug }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/create`, {
      pipelineId, 
      name,
      inParams,
      flagDebug
    })
  },
  startRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/start`, {
      pipelineId,
      pipelineRunId
    })
  },
  forwardRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/forward`, {
      pipelineId,
      pipelineRunId
    })
  },
  stopRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/stop`, {
      pipelineId,
      pipelineRunId
    })
  },
  deleteRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/run/delete`, {
      pipelineId,
      pipelineRunId
    })
  },

  // variable
  
  listVariable({ projectId, pipelineId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/variable/list?${projectId?'projectId='+projectId+'&':''}pipelineId=${pipelineId}`)
  },
  getVariable({ pipelineId, name }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/variable/get?pipelineId=${pipelineId}&name=${name}`)
  },
  createVariable({ pipelineId, name, hint, value, kind, paramType, required, password, order, defaultValue, options }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/variable/create`, {
      pipelineId,
      variable: {
        name,
        hint,
        value,
        kind,
        paramType,
        required,
        password,
        order,
        defaultValue,
        options,
      }
    })
  },
  updateVariable({ pipelineId, name, hint, value, kind, paramType, required, password, order, defaultValue, options }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/variable/update`, {
      pipelineId,
      variable: {
        name,
        hint,
        value,
        kind,
        paramType,
        required,
        password,
        order,
        defaultValue,
        options,
      }
    })
  },
  deleteVariable({ pipelineId, variableName }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/variable/delete`, {
      pipelineId,
      variableName,
    })
  },

  // terminal run

  createTerminalRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/create`, {
      pipelineId,
      pipelineRunId,
    })
  },

  destroyTerminalRun({ pipelineId, pipelineRunId, terminalRunInstanceUuid }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/destroy`, {
      pipelineId,
      pipelineRunId,
      terminalRunInstanceUuid,
    })
  },

  inputTerminalRun({ pipelineId, pipelineRunId, terminalRunInstanceUuid, message }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/terminal/run/message`, {
      pipelineId,
      pipelineRunId,
      terminalRunInstanceUuid,
      message,
    })
  },

  // explorer run

  createExplorerRun({ pipelineId, pipelineRunId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/create`, {
      pipelineId,
      pipelineRunId,
    })
  },

  destroyExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/destroy`, {
      pipelineId,
      pipelineRunId,
      explorerRunInstanceUuid,
    })
  },

  listFileExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid, path }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/file/list?pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&explorerRunInstanceUuid=${explorerRunInstanceUuid}&path=${encodeURIComponent(path)}`)
  },

  downloadFileExplorerRun({ projectId, pipelineId, pipelineRunId, explorerRunInstanceUuid, path }){
    let accessToken = store().getters.accessToken
    let teamId = store().state.workspace.teamId || ''
    window.open(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/file/download?projectId=${projectId}&pipelineId=${pipelineId}&pipelineRunId=${pipelineRunId}&explorerRunInstanceUuid=${explorerRunInstanceUuid}&path=${encodeURIComponent(path)}&auth=${encodeURIComponent(accessToken)}&teamId=${teamId}`, '_self')
  },  
  
  uploadFileExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid, dir, file, progressCallback, fCancelToken }){
    let formData = new FormData()
    
    const path = `${dir?dir+'/':''}${file.name}`
    formData.append('pipelineId', pipelineId)
    formData.append('pipelineRunId', pipelineRunId)
    formData.append('explorerRunInstanceUuid', explorerRunInstanceUuid)
    formData.append('path', path)
    formData.append('file', file)

    let time0 = new Date().getTime()
    let lastLoaded = 0

    return request().post(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/file/upload`, 
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: progressEvent => {
          let progress = progressEvent.loaded / progressEvent.total
          let time1 = new Date().getTime()
          if(progress < 1 && time1 - time0 < 1000){
            return
          }

          let speed = (progressEvent.loaded - lastLoaded) / ((time1 - time0) / 1000)

          time0 = time1
          lastLoaded = progressEvent.loaded

          if(progressCallback){
            progressCallback(path, speed, progress)
          }
        },
        cancelToken: new CancelToken(function executor(c) {
          if(fCancelToken){
            fCancelToken(path, c)
          }
        }),
      })
  },

  stopUploadFileExplorerRun({ pipelineId, pipelineRunId, explorerRunInstanceUuid, path }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/explorer/run/file/upload/stop`, {
      pipelineId,
      pipelineRunId,
      explorerRunInstanceUuid,
      path,
    })
  },

  // webhook trigger

  getWebhookTrigger({ pipelineId, pipelineWebhookTriggerId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/trigger/webhook/get?pipelineId=${pipelineId}&pipelineWebhookTriggerId=${pipelineWebhookTriggerId}`)
  },

  listWebhookTrigger({ pipelineId, orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/trigger/webhook/list?pipelineId=${pipelineId}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },

  createWebhookTrigger({ pipelineId, sourceType, eventType, tag, inParams }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/trigger/webhook/create`, {
      pipelineId,
      sourceType,
      eventType,
      tag,
      inParams,
    })
  },

  updateWebhookTrigger({ pipelineId, pipelineWebhookTriggerId, sourceType, eventType, tag, inParams }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/trigger/webhook/update`, {
      pipelineId,
      pipelineWebhookTriggerId,
      sourceType,
      eventType,
      tag,
      inParams,
    })
  },

  deleteWebhookTrigger({ pipelineId, pipelineWebhookTriggerId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/trigger/webhook/delete`, {
      pipelineId,
      pipelineWebhookTriggerId,
    })
  },

  // pending

  listPending({ pipelineId, orderField = '', orderType = '', pageNo = '', pageSize = '' }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/pending/list?pipelineId=${pipelineId}&pageNo=${pageNo}&pageSize=${pageSize}&orderField=${encodeURIComponent(orderField)}&orderType=${orderType}`)
  },

  getPending({ pipelineId, pipelinePendingId }){
    return request().get(`${API_PIPELINE_PREFIX}/pipeline/pending/get?pipelineId=${pipelineId}&pipelinePendingId=${pipelinePendingId}`)
  },

  deletePending({ pipelineId, pipelinePendingId }){
    return request().post(`${API_PIPELINE_PREFIX}/pipeline/pending/delete`, {
      pipelineId,
      pipelinePendingId,
    })
  },

}
