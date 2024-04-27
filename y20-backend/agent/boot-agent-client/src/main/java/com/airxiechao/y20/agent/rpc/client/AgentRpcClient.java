package com.airxiechao.y20.agent.rpc.client;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.client.RpcClient;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.core.rpc.RpcClientCaller;
import com.airxiechao.axcboot.core.rpc.RpcReg;
import com.airxiechao.axcboot.crypto.SslUtil;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.axcboot.util.IpUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.pojo.config.AgentClientJksConfig;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.agent.rpc.api.server.IAgentAgentServerRpc;
import com.airxiechao.y20.agent.rpc.param.RegisterAgentRpcParam;
import com.airxiechao.y20.auth.pojo.exception.InvalidAccessTokenException;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.git.GitUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.util.OsUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentRpcClient extends RpcClient implements IAgentRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(AgentRpcClient.class);
    private static final AgentClientConfig config = ConfigFactory.get(AgentClientConfig.class);
    private static final AgentClientJksConfig jksConfig = ConfigFactory.get(AgentClientJksConfig.class);
    private static final int MAX_WORKER_THREAD = 10;
    private static final int RECONNECT_DELAY_SECS = 1;

    private String serverIp;
    private String agentId;
    private String accessToken;
    private RpcClientCaller caller;

    public AgentRpcClient(String serverIp, String agentId, String accessToken) {
        super("agent rpc client");

        this.serverIp = serverIp;
        this.agentId = agentId;
        this.accessToken = accessToken;
        this.caller = new RpcClientCaller(this);

        this.config(serverIp, config.getServerRpcPort(), MAX_WORKER_THREAD, RECONNECT_DELAY_SECS,
                (authToken, scope, item, mode) -> {
                    return true;
                }, (ctx) -> {
                    logger.info("agent client connect");

                    boolean registered = false;
                    try {
                        registered = registerAgent(ctx);
                        if (!registered) {
                            stop();
                        }
                    } catch (Exception e) {
                        logger.error("register agent [{}] error", agentId, e);
                    }

                    logger.info("register agent [{}] -> {}", agentId, registered);
                }, (ctx, client) -> {
                    logger.info("agent client disconnect");
                });

        try {
            logger.info("config agent client to use ssl");
            this.useSsl(
                    SslUtil.buildKeyManagerFactory(new JavaResourceFs(), jksConfig.getClientJks(), jksConfig.getClientJksPassword()),
                    SslUtil.buildReloadableTrustManager(new JavaResourceFs(), jksConfig.getTrustJks(), jksConfig.getTrustJksPassword()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.registerRpcHandlerIfExists();

        // register to global instance
        Global.getInstance().registerImpl(IAgentRpcClient.class, this);
    }

    private void registerRpcHandlerIfExists() {
        RpcReg rpcReg = new RpcReg(Meta.getProjectPackageName(), this);
        rpcReg.registerHandlerIfExists(null);
    }

    @Override
    public Response callServer(String type, Map payload) {
        // add auth token to request
        payload.put("auth", accessToken);
        return super.callServer(type, payload);
    }

    @Override
    public List<ChannelHandlerContext> getAllChannelHandlerContext() {
        List<ChannelHandlerContext> list = new ArrayList<>();
        ChannelHandlerContext ctx = super.getRpcContext().getContext();
        if (null == ctx) {
            return list;
        }

        list.add(ctx);
        return list;
    }

    @Override
    public boolean registerAgent(ChannelHandlerContext ctx) throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        String ip = IpUtil.getIp(true);
        String version = GitUtil.getGitVersion();

        Response resp = this.callMaster(ctx, IAgentAgentServerRpc.class).registerAgent(
                new RegisterAgentRpcParam(null, this.agentId, version, localHost.getHostName(), OsUtil.getOs(), ip));
        if (resp.isSuccess()) {
            return true;
        } else if (resp.getMessage().equals(InvalidAccessTokenException.INVALID_ACCESS_TOKEN)){
            return false;
        } else {
            throw new Exception(resp.getMessage());
        }
    }

    @Override
    public <T> T callMaster(ChannelHandlerContext ctx, Class<T> cls) {
        return this.caller.get(cls);
    }

}
