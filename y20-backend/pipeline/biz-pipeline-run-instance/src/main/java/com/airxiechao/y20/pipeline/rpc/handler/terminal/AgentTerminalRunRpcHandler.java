package com.airxiechao.y20.pipeline.rpc.handler.terminal;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rpc.EnhancedRpcUtil;
import com.airxiechao.y20.pipeline.pubsub.event.terminal.EventTerminalRunOutputPush;
import com.airxiechao.y20.pipeline.rpc.api.IAgentTerminalRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.terminal.PushTerminalRunOutputRpcParam;

public class AgentTerminalRunRpcHandler implements IAgentTerminalRunRpc {
    @Override
    public Response pushTerminalRunOutput(Object exc) {
        RpcExchange rpcExchange = (RpcExchange) exc;
        PushTerminalRunOutputRpcParam param = null;
        try {
            param = EnhancedRpcUtil.getObjectParamWithAuth(rpcExchange, PushTerminalRunOutputRpcParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // todo: check ownership

        EventBus.getInstance().publish(
                new EventTerminalRunOutputPush(
                        param.getPipelineRunInstanceUuid(), param.getTerminalRunInstanceUuid(), param.getOutput()));

        return new Response();
    }
}
