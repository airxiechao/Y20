package com.airxiechao.y20.pipeline.rpc.handler.step;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.communication.rpc.util.RpcUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.pool.AgentRpcClientThreadPool;
import com.airxiechao.y20.pipeline.rpc.api.IAgentStepRunRpc;
import com.airxiechao.y20.pipeline.rpc.api.ILocalStepRpc;
import com.airxiechao.y20.pipeline.rpc.param.step.PushStepRunLogRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.step.RunStepRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.step.StepRunCallbackRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.step.StopStepRpcParam;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.LocalStepRunInstanceFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class LocalStepRpcHandler implements ILocalStepRpc {

    private static final Logger logger = LoggerFactory.getLogger(LocalStepRpcHandler.class);
    private IAgentRpcClient agentRpcClient = Global.get(IAgentRpcClient.class);
    private ThreadPoolExecutor executor = AgentRpcClientThreadPool.get().getExecutor();

    public LocalStepRpcHandler(){
    }

    @Override
    public Response runStep(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;

        ChannelHandlerContext ctx = rpcExchange.getCtx();
        RunStepRpcParam param = RpcUtil.getObjectParam(rpcExchange, RunStepRpcParam.class);

        logger.info("pipeline run [uuid:{}] step [type:{}, uuid:{}] start [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                param.getEnv().getPipelineRunInstanceUuid(), param.getStep().getType(), param.getStepRunInstanceUuid(),
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        AbstractStepRunInstance stepRun = LocalStepRunInstanceFactory.getInstance().buildLocalRun(
                param.getStep(), param.getStepRunInstanceUuid(), param.getEnv());
        stepRun.assembleAgentRpcClientContext(agentRpcClient, ctx);

        // run
        CompletableFuture<Response> future = stepRun.startAsync(executor).thenApply(resp -> {
            logger.info("pipeline run [uuid:{}] step [type:{}, uuid:{}] -> {}, {}",
                    param.getEnv().getPipelineRunInstanceUuid(), param.getStep().getType(), param.getStepRunInstanceUuid(),
                    true, resp.getData());
            return resp;
        }).exceptionally(e -> {
            logger.error("pipeline run [uuid:{}] step [type:{}, uuid:{}] error",
                    param.getEnv().getPipelineRunInstanceUuid(), param.getStep().getType(), param.getStepRunInstanceUuid(), e);

            Response resp = new Response().error(e.getMessage());
            return resp;
        }).whenComplete((resp, e) -> {
            stepRun.finishLog();
            LocalStepRunInstanceFactory.getInstance().removeLocalRun(stepRun.getStepRunInstanceUuid());

            // callback
            Response callBackResp = agentRpcClient.callMaster(ctx, IAgentStepRunRpc.class).stepRunCallback(new StepRunCallbackRpcParam(
                    param.getEnv().getPipelineRunInstanceUuid(),
                    param.getStepRunInstanceUuid(),
                    resp
            ));
        });

        // log
        CompletableFuture.runAsync(() -> {
            InputStream logInputStream = stepRun.getLogInputStream();
            try{
                StreamUtil.readStringInputStream(logInputStream, 200, Charset.forName("UTF-8"), (log) -> {
                    Response resp = agentRpcClient.callMaster(ctx, IAgentStepRunRpc.class).pushStepRunOutput(new PushStepRunLogRpcParam(
                            param.getEnv().getPipelineRunInstanceUuid(),
                            param.getStepRunInstanceUuid(),
                            log));

                    if(!resp.isSuccess()){
                        throw new RuntimeException(resp.getMessage());
                    }
                });
            }catch (Exception e){
                future.completeExceptionally(e);
            }finally {
                // end of log
                agentRpcClient.callMaster(ctx, IAgentStepRunRpc.class).pushStepRunOutput(new PushStepRunLogRpcParam(
                        param.getEnv().getPipelineRunInstanceUuid(),
                        param.getStepRunInstanceUuid(),
                        "\0"
                ));
            }
        });

        return new Response();
    }

    @Override
    public Response stopStep(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;

        StopStepRpcParam param = RpcUtil.getObjectParam(rpcExchange, StopStepRpcParam.class);

        logger.info("stop step run [{}]", param.getStepRunInstanceUuid());

        AbstractStepRunInstance stepRun = LocalStepRunInstanceFactory.getInstance().getLocalRun(param.getStepRunInstanceUuid());
        if (null == stepRun) {
            return new Response().error(String.format("no step run [%s]", param.getStepRunInstanceUuid()));
        }

        Response resp = stepRun.stop();
        if(resp.isSuccess()){
            logger.info("stop step run [{}, {}] -> {}",
                    param.getStepRunInstanceUuid(), stepRun.getType(), true);
        }else{
            logger.info("stop step run [{}, {}] error: {}", stepRun.getType(), param.getStepRunInstanceUuid(), resp.getMessage());
        }

        return resp;
    }
}
