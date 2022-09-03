package com.airxiechao.y20.agent.server.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.agent.rest.param.CallAgentParam;
import com.airxiechao.y20.agent.rest.api.IServiceAgentServerRest;
import com.airxiechao.y20.agent.rest.param.IsAgentActiveParam;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcServer;

import com.airxiechao.y20.common.core.global.Global;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAgentServerRestHandler implements IServiceAgentServerRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAgentServerRestHandler.class);
    private IAgentRpcServer agentRpcServer = Global.get(IAgentRpcServer.class);

    @Override
    public Response callAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CallAgentParam param = RestUtil.rawJsonData(exchange, CallAgentParam.class);

        Response resp = agentRpcServer.callAgent(
                param.getClientId(), param.getInterfaceName(), param.getMethodName(), param.getMethodParam());

        return resp;
    }

    @Override
    public Response isAgentActive(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        IsAgentActiveParam param = RestUtil.queryData(exchange, IsAgentActiveParam.class);

        boolean active = agentRpcServer.isAgentActive(param.getClientId());
        if(!active){
            return new Response().error();
        }

        return new Response();
    }

}
