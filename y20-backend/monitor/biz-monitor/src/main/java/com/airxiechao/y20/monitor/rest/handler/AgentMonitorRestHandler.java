package com.airxiechao.y20.monitor.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.monitor.biz.api.IMonitorBiz;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.airxiechao.y20.monitor.pojo.MonitorPipelineActionParam;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorActionType;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorStatus;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.ListMonitorParam;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorStatusParam;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCreatePipelineRunParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceGetPipelineBasicParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceStartPipelineRunParam;
import com.alibaba.fastjson.JSONObject;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentMonitorRestHandler implements IAgentMonitorRest {

    private static final Logger logger = LoggerFactory.getLogger(AgentMonitorRestHandler.class);

    private IMonitorBiz monitorBiz = Biz.get(IMonitorBiz.class);

    @Override
    public Response<List<Monitor>> list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListMonitorParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListMonitorParam.class, false);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // list
        List<Monitor> list = monitorBiz.list(
                param.getUserId(),
                param.getProjectId(),
                param.getAgentId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> record.toPojo()).collect(Collectors.toList());

        return new Response<List<Monitor>>().data(list);
    }

    @Override
    public Response updateStatus(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateMonitorStatusParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateMonitorStatusParam.class, false);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        MonitorRecord oldMonitorRecord = monitorBiz.getById(param.getUserId(), param.getProjectId(), param.getMonitorId());
        if(null == oldMonitorRecord){
            return new Response().error("no monitor");
        }
        Monitor oldMonitor = oldMonitorRecord.toPojo();

        boolean updated = monitorBiz.updateStatus(
                param.getUserId(), param.getProjectId(), param.getMonitorId(), param.getStatus());
        if(!updated){
            return new Response().error();
        }

        // 状态转变为错误时，执行动作
        if (!EnumMonitorStatus.ERROR.equals(oldMonitor.getStatus()) && EnumMonitorStatus.ERROR.equals(param.getStatus())) {
            if(EnumMonitorActionType.PIPELINE.equals(oldMonitor.getActionType())){
                MonitorPipelineActionParam pipelineActionParam = ((JSONObject)oldMonitor.getActionParam()).toJavaObject(MonitorPipelineActionParam.class);

                if(existsPipeline(oldMonitor.getUserId(), pipelineActionParam.getProjectId(), pipelineActionParam.getPipelineId())){
                    logger.info("monitor [{}] trigger pipeline [{}]", oldMonitor.getMonitorId(), pipelineActionParam.getPipelineId());
                    triggerPipeline(oldMonitor.getName(), pipelineActionParam.getPipelineId(), pipelineActionParam.getInParams());
                }
            }
        }

        return new Response();
    }

    private boolean existsPipeline(long userId, long projectId, long pipelineId){
        Response<PipelineBasicVo> pipelineResp = ServiceRestClient.get(IServicePipelineRest.class).getPipelineBasic(
                new ServiceGetPipelineBasicParam(
                        userId, projectId, pipelineId
                ));

        if(!pipelineResp.isSuccess()){
            logger.error("no pipeline");
            return false;
        }

        return true;
    }

    private void triggerPipeline(String monitorName, long pipelineId, Map<String, String> inParams) {
        Response<Long> createResp =  ServiceRestClient.get(IServicePipelineRest.class).createPipelineRun(
                new ServiceCreatePipelineRunParam(
                        pipelineId,
                        String.format("监视 %s", monitorName),
                        inParams,
                        false
                ));

        if(!createResp.isSuccess()){
            logger.error("create pipeline [{}] run error: {}", pipelineId, createResp.getMessage());
            return;
        }

        Long pipelineRunId = createResp.getData();
        Response<Long> startResp =  ServiceRestClient.get(IServicePipelineRest.class).startPipelineRun(
                new ServiceStartPipelineRunParam(pipelineRunId));

        if(!startResp.isSuccess()){
            logger.error("start pipeline [{}] run [{}] error: {}", pipelineId, pipelineRunId, startResp.getMessage());
            return;
        }

        logger.info("start pipeline [{}] run [{}] complete", pipelineId, pipelineRunId);
    }

}
