package com.airxiechao.y20.monitor.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
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
import com.airxiechao.y20.monitor.rest.api.IUserMonitorRest;
import com.airxiechao.y20.monitor.rest.param.*;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCreatePipelineRunParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceGetPipelineBasicParam;
import com.alibaba.fastjson.JSONObject;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserMonitorRestHandler implements IUserMonitorRest {

    private static final Logger logger = LoggerFactory.getLogger(UserMonitorRestHandler.class);

    private IMonitorBiz monitorBiz = Biz.get(IMonitorBiz.class);

    @Override
    public Response list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListMonitorParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListMonitorParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // count
        long count = monitorBiz.count(param.getUserId(), param.getProjectId(), param.getAgentId(), param.getName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -5);
        Date expireTime = cal.getTime();

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
        ).stream().map(record -> {
            Monitor monitor = record.toPojo();
            Date lastUpdateTime = monitor.getLastUpdateTime();
            if(null != lastUpdateTime && lastUpdateTime.before(expireTime)){
                monitor.setStatus(EnumMonitorStatus.UNKNOWN);
            }

            return monitor;
        }).collect(Collectors.toList());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response get(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetMonitorParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetMonitorParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        MonitorRecord record = monitorBiz.getById(param.getUserId(), param.getProjectId(), param.getMonitorId());
        return new Response().data(record.toPojo());
    }

    @Override
    public Response updateBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateMonitorBasicParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateMonitorBasicParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // 检查动作流水线是否存在
        if(EnumMonitorActionType.PIPELINE.equals(param.getActionType())){
            MonitorPipelineActionParam pipelineActionParam = ((JSONObject)param.getActionParam()).toJavaObject(MonitorPipelineActionParam.class);
            if(!existsPipeline(param.getUserId(), pipelineActionParam.getProjectId(), pipelineActionParam.getPipelineId())){
                return new Response().error("no pipeline");
            }
        }

        boolean updated = monitorBiz.updateBasic(
                param.getUserId(), param.getProjectId(), param.getMonitorId(),
                param.getAgentId(), param.getName(), param.getType(), param.getTarget(), param.getActionType(), param.getActionParam());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response create(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateMonitorParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreateMonitorParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // 检查动作流水线是否存在
        if(EnumMonitorActionType.PIPELINE.equals(param.getActionType())){
            MonitorPipelineActionParam pipelineActionParam = ((JSONObject)param.getActionParam()).toJavaObject(MonitorPipelineActionParam.class);
            if(!existsPipeline(param.getUserId(), pipelineActionParam.getProjectId(), pipelineActionParam.getPipelineId())){
                return new Response().error("no pipeline");
            }
        }

        MonitorRecord record = monitorBiz.create(
                param.getUserId(), param.getProjectId(), param.getAgentId(),
                param.getName(), param.getType(), param.getTarget(), param.getActionType(), param.getActionParam());
        if(null == record){
            return new Response().error();
        }

        return new Response().data(record.getId());
    }

    @Override
    public Response delete(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteMonitorParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeleteMonitorParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean deleted = monitorBiz.deleteById(param.getUserId(), param.getProjectId(), param.getMonitorId());
        if(!deleted){
            return new Response().error();
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
}
