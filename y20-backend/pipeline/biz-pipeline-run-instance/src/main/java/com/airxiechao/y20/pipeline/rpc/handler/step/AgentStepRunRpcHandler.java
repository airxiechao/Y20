package com.airxiechao.y20.pipeline.rpc.handler.step;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.common.core.rpc.EnhancedRpcUtil;
import com.airxiechao.y20.pipeline.pubsub.event.step.EventStepRunOutputPush;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.CallbackPipelineRunStepRunParam;
import com.airxiechao.y20.pipeline.rpc.api.IAgentStepRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.step.PushStepRunLogRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.step.StepRunCallbackRpcParam;

public class AgentStepRunRpcHandler implements IAgentStepRunRpc {
    @Override
    public Response pushStepRunOutput(Object exc) {
        RpcExchange rpcExchange = (RpcExchange)exc;

        PushStepRunLogRpcParam param = null;
        try {
            param = EnhancedRpcUtil.getObjectParamWithAuth(rpcExchange, PushStepRunLogRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // todo: check ownership
        EventBus.getInstance().publish(
                new EventStepRunOutputPush(param.getPipelineRunInstanceUuid(), param.getStepRunInstanceUuid(), param.getOutput()));

        return new Response();
    }

    @Override
    public Response stepRunCallback(Object exc) {
        RpcExchange rpcExchange = (RpcExchange)exc;

        StepRunCallbackRpcParam param = null;
        try {
            param = EnhancedRpcUtil.getObjectParamWithAuth(rpcExchange, StepRunCallbackRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // todo: check ownership

        CallbackPipelineRunStepRunParam restParam = new CallbackPipelineRunStepRunParam();

        String pipelineRunInstanceUuid = param.getPipelineRunInstanceUuid();
        restParam.setPipelineRunInstanceUuid(pipelineRunInstanceUuid);
        restParam.setStepRunInstanceUuid(param.getStepRunInstanceUuid());
        restParam.setResponse(param.getResponse());

        Response resp = ServicePipelineRunInstanceRestClient.get(IServicePipelineRunInstanceRest.class, pipelineRunInstanceUuid)
                .callbackPipelineStepRunInstance(restParam);

        return resp;
    }
}
