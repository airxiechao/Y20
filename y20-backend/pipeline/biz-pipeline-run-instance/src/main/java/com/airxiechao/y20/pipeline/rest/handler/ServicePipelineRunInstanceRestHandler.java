package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.biz.api.IPipelineRunInstanceBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.*;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServicePipelineRunInstanceRestHandler implements IServicePipelineRunInstanceRest {

    private static final Logger logger = LoggerFactory.getLogger(ServicePipelineRunInstanceRestHandler.class);
    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);
    private IPipelineRunInstanceBiz pipelineRunInstanceBiz = Biz.get(IPipelineRunInstanceBiz.class);

    @Override
    public Response listPipelineRunInstance(Object exc) {
        List<IPipelineRunInstance> list = pipelineRunInstanceBiz.listRunInstance();
        return new Response().data(list);
    }

    @Override
    public Response createPipelineRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineRunInstanceParam param = RestUtil.rawJsonData(exchange, CreatePipelineRunInstanceParam.class);

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        PipelineRunDetailVo pipelineRunDetail = pipelineBiz.getPipelineRunVo(pipelineRunRecord.getUserId(), pipelineRunRecord.getProjectId(), pipelineRunRecord.getPipelineId(), pipelineRunRecord.getId());

        IPipelineRunInstance runInstance = pipelineRunInstanceBiz.createRunInstance(pipelineRunDetail);
        return new Response().data(runInstance.getPipelineRunInstanceUuid());
    }

    @Override
    public Response startPipelineRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StartPipelineRunInstanceParam param = RestUtil.rawJsonData(exchange, StartPipelineRunInstanceParam.class);
        IPipelineRunInstance runInstance = pipelineRunInstanceBiz.getRunInstance(param.getPipelineRunInstanceUuid());

        if(param.getFlagDebug()){
            runInstance.iterateStepAsync();
        }else{
            runInstance.startAsync();
        }

        return new Response();
    }

    @Override
    public Response forwardPipelineRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ForwardPipelineRunInstanceParam param = RestUtil.rawJsonData(exchange, ForwardPipelineRunInstanceParam.class);
        IPipelineRunInstance runInstance = pipelineRunInstanceBiz.getRunInstance(param.getPipelineRunInstanceUuid());

        runInstance.nextStepAsync();
        return new Response();
    }

    @Override
    public Response stopPipelineRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopPipelineRunInstanceParam param = RestUtil.rawJsonData(exchange, StopPipelineRunInstanceParam.class);
        IPipelineRunInstance runInstance = pipelineRunInstanceBiz.getRunInstance(param.getPipelineRunInstanceUuid());

        runInstance.stopAsync();
        return new Response();
    }

    @Override
    public Response callbackPipelineStepRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CallbackPipelineRunStepRunParam param = RestUtil.rawJsonData(exchange, CallbackPipelineRunStepRunParam.class);

        pipelineRunInstanceBiz.stepRunInstanceCallback(
                param.getPipelineRunInstanceUuid(), param.getStepRunInstanceUuid(), param.getResponse());

        return new Response();
    }
}
