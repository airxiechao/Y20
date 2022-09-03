package com.airxiechao.y20.agent.rpc.api.server;

import com.airxiechao.axcboot.communication.common.Response;

public interface IAgentRpcServer {

    Response callAgent(String clientId, String interfaceName, String methodName, Object methodParam);

    boolean isAgentActive(String clientId);
}
