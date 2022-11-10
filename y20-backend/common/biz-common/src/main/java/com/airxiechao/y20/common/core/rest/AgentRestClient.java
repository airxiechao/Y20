package com.airxiechao.y20.common.core.rest;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.core.rest.AbstractRestClient;
import com.airxiechao.axcboot.util.MapBuilder;
import com.airxiechao.y20.agent.rpc.api.server.IAgentAgentServerRpc;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.agent.rpc.param.GetRestServicePathRpcParam;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AgentRestClient extends AbstractRestClient {

    private static final Logger logger = LoggerFactory.getLogger(AgentRestClient.class);

    private IAgentRpcClient agentRpcClient;
    private ChannelHandlerContext agentRpcCtx;
    private String host;
    private Integer port;
    private boolean useSsl;
    private String accessToken;

    public AgentRestClient(
            IAgentRpcClient agentRpcClient, ChannelHandlerContext agentRpcCtx,
            String host, Integer port, boolean useSsl, String accessToken
    ){
        this.agentRpcClient = agentRpcClient;
        this.agentRpcCtx = agentRpcCtx;
        this.host = host;
        this.port = port;
        this.useSsl = useSsl;
        this.accessToken = accessToken;
    }

    public <T> T get(Class<T> cls){
        Map<String, String> headers = new MapBuilder<String, String>()
                .put("auth", accessToken)
                .build();

        return getProxy(cls, null, headers, null, 0, useSsl, null, null);
    }

    public <T> T get(Class<T> cls, BiConsumer<Long, Long> totalAndSpeedConsumer, Supplier<Boolean> stopSupplier){
        Map<String, String> headers = new MapBuilder<String, String>()
                .put("auth", accessToken)
                .build();

        return getProxy(cls, null, headers, null, 0, useSsl, totalAndSpeedConsumer, stopSupplier);
    }

    public <T> T get(Class<T> cls, OutputStream outputStream, BiConsumer<Long, Long> totalAndSpeedConsumer, Supplier<Boolean> stopSupplier){
        Map<String, String> headers = new MapBuilder<String, String>()
                .put("auth", accessToken)
                .build();

        return getProxy(cls, null, headers, null, 0, useSsl, outputStream, totalAndSpeedConsumer, stopSupplier);
    }

    @Override
    public String getUrl(Method method, String serviceTag) throws Exception{
        // get service path
        String className = method.getDeclaringClass().getName();
        Response<String> getServicePathResp = agentRpcClient.callMaster(agentRpcCtx, IAgentAgentServerRpc.class).getRestServicePath(
                new GetRestServicePathRpcParam(className));
        if(!getServicePathResp.isSuccess()){
            logger.error("get service path error: {}", getServicePathResp.getMessage());
            throw new Exception(getServicePathResp.getMessage());
        }

        String servicePath = getServicePathResp.getData();

        String protocol = useSsl ? "https" : "http";
        if(null == port){
            port = useSsl ? 443 : 80;
        }

        return String.format("%s://%s:%d/%s%s", protocol, host, port, servicePath, RestUtil.getMethodPath(method));
    }

}
