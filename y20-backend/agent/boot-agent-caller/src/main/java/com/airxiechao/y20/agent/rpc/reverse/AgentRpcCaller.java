package com.airxiechao.y20.agent.rpc.reverse;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.communication.rpc.client.RpcClient;
import com.airxiechao.axcboot.core.rpc.RpcReg;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.agent.biz.api.IAgentBiz;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcClientRegister;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcServer;
import com.airxiechao.y20.agent.pojo.Agent;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus.STATUS_OFFLINE;
import static com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus.STATUS_ONLINE;

public class AgentRpcCaller implements IAgentRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(AgentRpcCaller.class);

    private Map<String, AgentRpcCallerClient> agentClientMap = new ConcurrentHashMap<>();
    private IAgentBiz agentBiz = Biz.get(IAgentBiz.class);

    public AgentRpcCaller() {

    }

    public void connectAgent(String calleeIp){
        AgentRpcCallerClient client = new AgentRpcCallerClient(calleeIp);
        client.start();
    }

    public void stop(){
        for(AgentRpcCallerClient client : agentClientMap.values()){
            if(null != client){
                client.stop();
            }
        }
    }

    @Override
    public Response callAgent(String clientId, String interfaceName, String methodName, Object methodParam) {
        AgentRpcCallerClient client = agentClientMap.get(clientId);
        if(null == client){
           return new Response().error("no agent client");
        }

        try {
            // method
            Class<?> interfaceCls = Class.forName(interfaceName);
            Method method = interfaceCls.getMethod(methodName, Object.class);
            if(null == method){
                throw new Exception(String.format("no method [%s.%s]", interfaceName, methodName));
            }

            // type
            Query query = interfaceCls.getAnnotation(Query.class);
            if(null == query){
                throw new Exception(String.format("no type in method [%s.%s]", interfaceName, methodName));
            }
            String type = query.value();

            // param
            Map<String, Object> param;
            if (methodParam instanceof Map) {
                param = (Map<String, Object>) methodParam;
            } else {
                param = ModelUtil.toMap(methodParam);
            }

            Response resp = client.callServer(type, ModelUtil.toMap(param));

            if(!resp.isSuccess()){
                logger.error("call agent client [{}] to handle [{}] error: {}", clientId, type, resp.getMessage());
            }

            return resp;

        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public boolean isAgentActive(String clientId) {
        AgentRpcCallerClient client = agentClientMap.get(clientId);
        if(null == client){
            return false;
        }

        return client.isServerActive();
    }

    /*****************************************************************************************
     *
     *                              node caller rpc client mixin
     *
     *****************************************************************************************/

    public class AgentRpcCallerClient extends RpcClient implements IAgentRpcClientRegister {

        private String calleeIp;
        private Agent agent;

        public AgentRpcCallerClient(String calleeIp){
            super("agent rpc caller client");

            this.calleeIp = calleeIp;

            this.config(calleeIp, 9001, 2, 1,
                    (authToken, scope, item, mode) -> {
                        return true;
                    }, null, (ChannelHandlerContext ctx, String client) -> {
                        agentClientMap.remove(client);
                        this.agent = null;

                        agentBiz.updateClient(agent.getUserId(), agent.getClientId(), null, null, null, null, null, STATUS_OFFLINE);
                    });

            this.registerRpcHandlerIfExists();
        }

        private void registerRpcHandlerIfExists(){
            RpcReg rpcReg = new RpcReg(Meta.getProjectPackageName(), this);
            rpcReg.registerHandlerIfExists(null);
        }

        @Override
        public String getClientByContext(ChannelHandlerContext ctx) {
            return null;
        }

        @Override
        public boolean registerClient(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os) {
            AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(userId, agentId);
            if(null == agentRecord){
                agentRecord = agentBiz.create(userId, agentId);
                if(null == agentRecord){
                    logger.error("register client create agent error");
                    return false;
                }
            }

            boolean updated = agentBiz.updateClient(userId, agentId, clientId, version, ip, hostName, os, STATUS_ONLINE);
            if(!updated){
                logger.error("register client update agent error");
                return false;
            }

            agentRecord = agentBiz.getByUserIdAndAgentId(userId, agentId);
            this.agent = agentRecord.toPojo();
            agentClientMap.put(agentId, this);

            return true;
        }

        public Agent getAgent() {
            return agent;
        }
    }
}
