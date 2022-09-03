package com.airxiechao.y20.agent.rpc.reverse;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.server.RpcServer;
import com.airxiechao.axcboot.core.rpc.RpcReg;
import com.airxiechao.axcboot.core.rpc.RpcServerCaller;
import com.airxiechao.axcboot.util.IpUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.rpc.api.server.IAgentAgentServerRpc;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.agent.rpc.param.RegisterAgentRpcParam;
import com.airxiechao.y20.common.git.GitUtil;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.util.OsUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AgentRpcCallee extends RpcServer implements IAgentRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(AgentRpcCallee.class);
    public static final int PORT = 9001;

    private String agentId;
    private RpcServerCaller caller;

    public AgentRpcCallee(String agentId) {
        super("agent rpc callee");

        this.agentId = agentId;
        this.caller = new RpcServerCaller(this);

        this.config("0.0.0.0", PORT, 2, 5,
                (authToken, scope, item, mode) -> {
                    return true;
                }, (ctx, client)->{
                    try {
                        registerAgent(ctx);
                    } catch (Exception e) {
                        logger.error("register agent error", e);
                    }
                }, null);

        this.registerRpcHandlerIfExists();
    }

    private void registerRpcHandlerIfExists(){
        RpcReg rpcReg = new RpcReg(Meta.getProjectPackageName(), this);
        rpcReg.registerHandlerIfExists(null);
    }

    @Override
    public List<ChannelHandlerContext> getAllChannelHandlerContext() {
        List<ChannelHandlerContext> list = new ArrayList<>();
        super.getAllRpcContext().forEach(rpcContext -> {
            list.add(rpcContext.getContext());
        });

        return list;
    }

    @Override
    public boolean registerAgent(ChannelHandlerContext ctx) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String ip = IpUtil.getIp(true);
        String version = GitUtil.getGitVersion();

        Response resp = this.callMaster(ctx, IAgentAgentServerRpc.class).registerAgent(
                new RegisterAgentRpcParam(null, this.agentId, version, localHost.getHostName(), OsUtil.getOs(), ip));

        return resp.isSuccess();
    }

    @Override
    public <T> T callMaster(ChannelHandlerContext ctx, Class<T> cls) {
        String client = this.getRouter().getClientByContext(ctx);
        return this.caller.get(cls, client);
    }

}
