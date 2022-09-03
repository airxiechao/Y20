package com.airxiechao.y20.agent.server.rpc.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestClientUtil;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.rpc.api.server.IAgentAgentServerRpc;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcClientRegister;
import com.airxiechao.y20.agent.rpc.param.GetRestServicePathRpcParam;
import com.airxiechao.y20.agent.rpc.param.RegisterAgentRpcParam;
import com.airxiechao.y20.common.core.cache.RestServiceCache;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.rpc.EnhancedRpcUtil;
import com.ecwid.consul.v1.health.model.HealthService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AgentAgentServerRpcHandler implements IAgentAgentServerRpc {

    private static final Logger logger = LoggerFactory.getLogger(AgentAgentServerRpcHandler.class);
    private static final String SERVICE_NAME_PREFIX = "y20-backend-";

    private IAgentRpcClientRegister clientRegister = Global.get(IAgentRpcClientRegister.class);

    public AgentAgentServerRpcHandler(){

    }

    @Override
    public Response registerAgent(Object exc) {
        RpcExchange exchange = (RpcExchange)exc;
        ChannelHandlerContext ctx = exchange.getCtx();

        RegisterAgentRpcParam param = null;
        try {
            param = EnhancedRpcUtil.getObjectParamWithAuth(exchange, RegisterAgentRpcParam.class);
        } catch (Exception e) {
            ctx.close();
            return new Response().error(e.getMessage());
        }

        String clientId = clientRegister.getClientByContext(ctx);
        boolean ret = clientRegister.registerClient(
                param.getUserId(), param.getAgentId(), clientId, param.getVersion(),
                param.getIp(), param.getHostName(), param.getOs());

        logger.info("node agent [userId:{}, agentId:{}, clientId:{}, version:{}, hostName:{}, os:{}, ip:{}] register -> {}",
                param.getUserId(), param.getAgentId(), clientId, param.getVersion(), param.getHostName(), param.getOs(), param.getIp(), ret);

        if(!ret){
            ctx.close();
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response<String> getRestServicePath(Object exc) {
        RpcExchange exchange = (RpcExchange)exc;

        GetRestServicePathRpcParam param = null;
        try {
            param = EnhancedRpcUtil.getObjectParamWithAuth(exchange, GetRestServicePathRpcParam.class);
        } catch (Exception e) {
            return new Response<String>().error(e.getMessage());
        }

        String className = param.getClassName();
        String serviceName = RestServiceCache.getInstance().getMethodServiceName(className);
        if(StringUtil.isBlank(serviceName)){
            return new Response<String>().error("no service name of " + className);
        }

        String consulServiceName = SERVICE_NAME_PREFIX+serviceName;
        HealthService service = null;
        try {
            service = RestClientUtil.getServiceFromConsul(consulServiceName, null);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        Map<String, String> meta = service.getService().getMeta();
        String basePath = meta.getOrDefault("basePath", "");

        String servicePath = String.format("/%s%s", serviceName, basePath);
        return new Response<String>().data(servicePath);
    }
}
