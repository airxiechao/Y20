package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRecord;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicPageVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineCountVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.*;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServicePipelineRestHandler implements IServicePipelineRest {

    private static final Logger logger = LoggerFactory.getLogger(ServicePipelineRestHandler.class);
    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    @Override
    public Response<PipelineBasicVo> getPipelineBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceGetPipelineBasicParam param;
        try {
            param = RestUtil.queryData(exchange, ServiceGetPipelineBasicParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );

        if(null == record){
            return new Response().error("no pipeline");
        }

        PipelineBasicVo basicVo = new PipelineBasicVo(record);

        return new Response().data(basicVo);
    }

    @Override
    public Response<PipelineCountVo> countPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCountPipelineParam  param;
        try {
            param = RestUtil.queryData(exchange, ServiceCountPipelineParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        long total = pipelineBiz.count(param.getUserId(), param.getProjectId(), null);
        long running = pipelineBiz.countPipelineRun(
                param.getUserId(), param.getProjectId(), null, null, null, true, true
        );

        PipelineCountVo pipelineCountVo = new PipelineCountVo(total, running);

        return new Response<PipelineCountVo>().data(pipelineCountVo);
    }

    @Override
    public Response<PipelineBasicPageVo> listScheduledPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceListScheduledPipelineParam  param;
        try {
            param = RestUtil.queryData(exchange, ServiceListScheduledPipelineParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // list
        List<PipelineBasicVo> list = pipelineBiz.listScheduledPipeline(
                param.getFromPipelineId(),
                param.getToPipelineId()
        ).stream().map(record -> new PipelineBasicVo((record))).collect(Collectors.toList());;

        // has more
        boolean hasMore = pipelineBiz.hasMore(param.getToPipelineId());

        PipelineBasicPageVo pageVo = new PipelineBasicPageVo(list, hasMore);

        return new Response<PipelineBasicPageVo>().data(pageVo);
    }

    @Override
    public Response<Long> createPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCreatePipelineParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCreatePipelineParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.create(
                param.getUserId(), param.getProjectId(), param.getName(), param.getDescription(), param.getFlagOneRun(),
                param.getSteps(), param.getVariables()
        );

        if(null == record){
            return new Response().error("create pipeline error");
        }

        return new Response<Long>().data(record.getId());
    }

    @Override
    public Response<Long> deletePipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceDeletePipelineParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, ServiceDeletePipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(param.getPipelineId());
        if(null == record){
            return new Response().error("no pipeline");
        }

        boolean delete = pipelineBiz.delete(record.getUserId(), record.getProjectId(), record.getId());
        if(!delete){
            return new Response().error("delete pipeline error");
        }

        return new Response();
    }

    @Override
    public Response<Long> deleteProjectAllPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceDeleteProjectAllPipelineParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, ServiceDeleteProjectAllPipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean delete = pipelineBiz.delete(param.getProjectId());
        if(!delete){
            return new Response().error("delete project's pipeline error");
        }

        return new Response();
    }

    @Override
    public Response<Set<String>> listPipelineRunRunningAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceGetPipelineRunRunningAgentParam param;
        try {
            param = RestUtil.queryData(exchange, ServiceGetPipelineRunRunningAgentParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Set<String> runningAgents = pipelineBiz.listPipelineRunRunningAgent(param.getUserId());
        return new Response<Set<String>>().data(runningAgents);
    }

    @Override
    public Response<PipelineRunDetailVo> getPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineRunParam param;
        try {
            param = RestUtil.queryData(exchange, GetPipelineRunParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        PipelineRunDetailVo vo = pipelineBiz.getPipelineRunVo(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );

        return new Response<PipelineRunDetailVo>().data(vo);
    }

    @Override
    public Response<Long> countPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CountPipelineRunParam param;
        try {
            param = RestUtil.queryData(exchange, CountPipelineRunParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        long count = pipelineBiz.countPipelineRun(param.getUserId(), param.getBeginTime(), param.getEndTime());
        return new Response<Long>().data(count);
    }

    @Override
    public Response<Long> createPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCreatePipelineRunParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCreatePipelineRunParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline
        PipelineRecord pipelineRecord = pipelineBiz.get(param.getPipelineId());
        if(null == pipelineRecord){
            return new Response().error("no pipeline");
        }

        // create run record
        PipelineRunRecord pipelineRunRecord = null;
        try {
            pipelineRunRecord = pipelineBiz.createPipelineRun(
                    pipelineRecord.toPojo(),
                    param.getName(), param.getInParams(), param.getFlagDebug());

            if(null == pipelineRunRecord){
                throw new Exception("create pipeline run error");
            }
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // create run instance
        Response createRunInstanceResp = ServiceRestClient.get(IServicePipelineRunInstanceRest.class).createPipelineRunInstance(
                new CreatePipelineRunInstanceParam(pipelineRunRecord.getId()));
        if(!createRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunRecord.getId(), EnumPipelineRunStatus.STATUS_FAILED, createRunInstanceResp.getMessage(), null);
            return new Response().error("create pipeline run instance error");
        }

        return new Response().data(pipelineRunRecord.getId());
    }

    @Override
    public Response<Long> startPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceStartPipelineRunParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceStartPipelineRunParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline run
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // start run instance
        Response startRunInstanceResp = ServicePipelineRunInstanceRestClient.get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid()).startPipelineRunInstance(
                new StartPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid(), pipelineRunRecord.getFlagDebug()));
        if(!startRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunRecord.getId(), EnumPipelineRunStatus.STATUS_FAILED, startRunInstanceResp.getMessage(), null);
            return new Response().error("start pipeline run instance error");
        }

        return new Response().data(pipelineRunRecord.getId());
    }
}
