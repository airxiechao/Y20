package com.airxiechao.y20.pipeline.rpc.handler.terminal;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.communication.rpc.util.RpcUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.pool.AgentRpcClientThreadPool;
import com.airxiechao.y20.pipeline.rpc.api.IAgentTerminalRunRpc;
import com.airxiechao.y20.pipeline.rpc.api.ILocalTerminalRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.terminal.CreateTerminalRunRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.terminal.DestroyTerminalRunRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.terminal.PushTerminalRunInputRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.terminal.PushTerminalRunOutputRpcParam;
import com.airxiechao.y20.pipeline.run.terminal.AbstractLocalTerminalRunInstance;
import com.airxiechao.y20.pipeline.run.terminal.LocalTerminalRunInstanceFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class LocalTerminalRunRpcHandler implements ILocalTerminalRunRpc {

    private static final Logger logger = LoggerFactory.getLogger(LocalTerminalRunRpcHandler.class);

    private IAgentRpcClient agentRpcClient = Global.get(IAgentRpcClient.class);
    private ThreadPoolExecutor executor = AgentRpcClientThreadPool.get().getExecutor();

    public LocalTerminalRunRpcHandler(){

    }

    @Override
    public Response createTerminalRun(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        ChannelHandlerContext ctx = rpcExchange.getCtx();

        CreateTerminalRunRpcParam param = RpcUtil.getObjectParam(rpcExchange, CreateTerminalRunRpcParam.class);

        AbstractLocalTerminalRunInstance terminalRun = LocalTerminalRunInstanceFactory.getInstance().createLocalTerminalRun(
                param.getTerminalType(), param.getDockerContainerId(), param.getWorkingDir());
        String terminalRunInstanceUuid = terminalRun.getTerminalRunInstanceUuid();

        // push terminal output
        logger.info("pipeline run [uuid:{}] terminal [type:{}, uuid:{}] log start [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                param.getPipelineRunInstanceUuid(), param.getTerminalType(), terminalRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());
        CompletableFuture.runAsync(() -> {
            try {
                StreamUtil.readStringInputStream(terminalRun.getInputStream(), 200, Charset.forName("UTF-8"), (output)->{
                    this.agentRpcClient.callMaster(ctx, IAgentTerminalRunRpc.class).pushTerminalRunOutput(
                            new PushTerminalRunOutputRpcParam(param.getPipelineRunInstanceUuid(), terminalRunInstanceUuid, output));
                });
            } catch (Exception e) {

            }
        }, executor);

        return new Response().data(terminalRunInstanceUuid);
    }

    @Override
    public Response destroyTerminalRun(Object exc) {
        RpcExchange rpcExchange = (RpcExchange)exc;
        DestroyTerminalRunRpcParam param = RpcUtil.getObjectParam(rpcExchange, DestroyTerminalRunRpcParam.class);
        LocalTerminalRunInstanceFactory.getInstance().destroyLocalTerminalRun(param.getTerminalRunInstanceUuid());
        return new Response();
    }

    @Override
    public Response pushTerminalRunInput(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;

        PushTerminalRunInputRpcParam param = RpcUtil.getObjectParam(rpcExchange, PushTerminalRunInputRpcParam.class);

        AbstractLocalTerminalRunInstance terminal = LocalTerminalRunInstanceFactory.getInstance().getLocalTerminalRun(param.getTerminalRunInstanceUuid());
        if(null == terminal){
            throw new Exception("no terminal run");
        }

        OutputStreamWriter writer = new OutputStreamWriter(terminal.getOutputStream());
        writer.write(param.getInput());
        writer.flush();

        return new Response();
    }

}
