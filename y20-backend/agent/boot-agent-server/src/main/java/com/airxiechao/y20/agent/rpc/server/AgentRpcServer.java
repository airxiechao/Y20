package com.airxiechao.y20.agent.rpc.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.communication.rpc.server.RpcServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.core.rpc.RpcReg;
import com.airxiechao.axcboot.crypto.SslUtil;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.agent.biz.api.IAgentBiz;
import com.airxiechao.y20.agent.pojo.config.AgentServerConfig;
import com.airxiechao.y20.agent.pojo.config.AgentServerJksConfig;
import com.airxiechao.y20.agent.pojo.constant.EnumAgentInjectName;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcServer;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.cache.AgentServiceTagCache;
import com.airxiechao.y20.agent.rpc.api.server.IAgentRpcClientRegister;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.rpc.EnhancedRpcUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import static com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus.STATUS_OFFLINE;
import static com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus.STATUS_ONLINE;

public class AgentRpcServer extends RpcServer implements IAgentRpcServer, IAgentRpcClientRegister {

    private static final Logger logger = LoggerFactory.getLogger(AgentRpcServer.class);

    private static final AgentServerConfig config = ConfigFactory.get(AgentServerConfig.class);
    private static final AgentServerJksConfig jksConfig = ConfigFactory.get(AgentServerJksConfig.class);
    public static final int PORT = config.getRpcPort();

    private static AgentRpcServer instance;
    public static AgentRpcServer getInstance() {
        if(null == instance){
            String uuid = Global.getInstance().inject(EnumAgentInjectName.AGENT_REST_SERVER_UUID);
            if(StringUtil.isBlank(uuid)){
                uuid = UuidUtil.random();
            }

            instance = new AgentRpcServer(uuid);
        }

        return instance;
    }

    private IAgentBiz agentBiz = Biz.get(IAgentBiz.class);

    private AgentRpcServer(String uuid) {
        super("agent rpc server", uuid);

        // only check "who you are" here.
        this.config("0.0.0.0", PORT, 2, 5, EnhancedRpcUtil::validateAccessToken,
                (ChannelHandlerContext ctx, String client) -> {
                    // when connected
                },
                (ChannelHandlerContext ctx, String client) -> {
                    // when disconnected
                    AgentRecord agentRecord = agentBiz.getByClientId(client);
                    if(null != agentRecord){
                        AgentServiceTagCache.getInstance().removeAgentServiceTag(client);
                        agentBiz.updateClient(agentRecord.getUserId(), agentRecord.getAgentId(), null, null, null, null, null, STATUS_OFFLINE);
                    }
                });

        this.setVerboseLog(null != config.getRpcVerbose() ? config.getRpcVerbose() : false);

        try{
            logger.info("config agent server to use ssl");
            this.useSsl(
                    SslUtil.buildKeyManagerFactory(new JavaResourceFs(), jksConfig.getServerJks(), jksConfig.getServerJksPassword()),
                    SslUtil.buildReloadableTrustManager(new JavaResourceFs(), jksConfig.getTrustJks(), jksConfig.getTrustJksPassword()));
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        this.registerRpcHandlerIfExists();

        // register to global instance
        Global.getInstance().registerImpl(IAgentRpcServer.class, this);
        Global.getInstance().registerImpl(IAgentRpcClientRegister.class, this);
    }

    private void registerRpcHandlerIfExists(){
        RpcReg rpcReg = new RpcReg(Meta.getProjectPackageName(), this);
        rpcReg.registerHandlerIfExists(null);
    }

    @Override
    public String getClientByContext(ChannelHandlerContext ctx) {
        return getRouter().getClientByContext(ctx);
    }

    @Override
    public boolean registerClient(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os){
        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(userId, agentId);
        if(null == agentRecord){
            agentRecord = agentBiz.create(userId, agentId);
            if(null == agentRecord){
                logger.error("register agent client error: cannot create agent [{}] record", agentId);
                return false;
            }
        }else{
            // check if agent is active
            String existedClientId = agentRecord.getClientId();
            if(!StringUtil.isBlank(existedClientId)){
                if(isClientActive(existedClientId)){
                    logger.error("register agent client error: agent [{}] is active", agentId);
                    return false;
                }
            }
        }

        boolean updated = agentBiz.updateClient(userId, agentId, clientId, version, ip, hostName, os, STATUS_ONLINE);
        if(!updated){
            logger.error("register agent client error: update agent [{}] error", agentId);
            return false;
        }

        AgentServiceTagCache.getInstance().setAgentServiceTag(clientId, getUuid());

        return true;
    }

    @Override
    public Response callAgent(String clientId, String interfaceName, String methodName, Object methodParam) {
        AgentRecord agentRecord = agentBiz.getByClientId(clientId);

        if(null == agentRecord){
            String error = String.format("no agent client [%s]", clientId);
            logger.error(error);
            return new Response().error(error);
        }

        try {
            // method
            Class<?> interfaceCls = Class.forName(interfaceName);
            Method method = interfaceCls.getMethod(methodName, Object.class);
            if(null == method){
                throw new Exception(String.format("no method [%s.%s]", interfaceName, methodName));
            }

            // type
            Query query = method.getAnnotation(Query.class);
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

            Response resp = callClient(clientId, type, ModelUtil.toMap(param));

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
        return isClientActive(clientId);
    }

}
