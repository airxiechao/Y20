package com.airxiechao.y20.agent.rpc.api.server;

import io.netty.channel.ChannelHandlerContext;

public interface IAgentRpcClientRegister {
    boolean registerClient(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os);
    String getClientByContext(ChannelHandlerContext ctx);
}
