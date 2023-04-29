package com.airxiechao.y20.agent.rpc.api.client;

import io.netty.channel.ChannelHandlerContext;

import java.net.UnknownHostException;
import java.util.List;

public interface IAgentRpcClient {
    List<ChannelHandlerContext> getAllChannelHandlerContext();
    boolean registerAgent(ChannelHandlerContext ctx) throws Exception;
    <T> T callMaster(ChannelHandlerContext ctx, Class<T> cls);
}
