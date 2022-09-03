package com.airxiechao.y20.pipeline.run.step;


import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public abstract class AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStepRunInstance.class);

    protected String stepRunInstanceUuid;
    protected Env env;

    protected StreamUtil.PipedStream pipedStream = new StreamUtil.PipedStream();

    public AbstractStepRunInstance(String stepRunInstanceUuid, Env env){
        this.stepRunInstanceUuid = StringUtil.isBlank(stepRunInstanceUuid) ? UuidUtil.random() : stepRunInstanceUuid;

        if(null == env){
            env = new Env();
        }
        if(StringUtil.isBlank(env.getPipelineRunInstanceUuid())){
            env.setPipelineRunInstanceUuid(UuidUtil.random());
        }
        this.env = env;
    }

    public void assembleAgentRpcClientContext(IAgentRpcClient agentRpcClient, ChannelHandlerContext agentRpcClientContext){

    }

    public abstract void assemble(PipelineStep step);
    protected abstract Response run() throws Exception;
    public abstract Response stop() throws Exception;

    public Response start() throws Exception {
        try{
            return run();
        }finally {
            this.pipedStream.flush();
        }
    }

    public void appendLogLine(String log){
        OutputStream outputStream = getLogOutputStream();
        try {
            log += "\r\n";
            outputStream.write(log.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("append log line error", e);
        }
    }

    public void appendLog(String log){
        OutputStream outputStream = getLogOutputStream();
        try {
            outputStream.write(log.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("append log error", e);
        }
    }

    public void finishLog(){
        this.pipedStream.close();
    }

    public CompletableFuture<Response> startAsync(Executor executor){
        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
            try {
                return start();
            } catch (Exception e) {
                return new Response().error(e.getMessage());
            }
        }, executor);

        return future;
    }

    public CompletableFuture<Response> startAsync(Consumer<Exception> exceptionConsumer, ThreadPoolExecutor executor){
        logger.info("pipeline run [uuid:{}] step run [uuid:{}] start async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                env.getPipelineRunInstanceUuid(), stepRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());

        CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> {
            try {
                return start();
            } catch (Exception e) {
                exceptionConsumer.accept(e);
                return new Response().error(e.getMessage());
            }
        }, executor);

        return future;
    }

    public CompletableFuture<Response> startAndConsumeLogAsync(Consumer<String> logConsumer,
                                                               Consumer<Exception> exceptionConsumer,
                                                               ThreadPoolExecutor executor){
        CompletableFuture<Response> future = startAsync(exceptionConsumer, executor);

        // consume log
        InputStream inputStream = getLogInputStream();
        logger.info("pipeline run [uuid:{}] step run [uuid:{}] start and consume log async [pool:{}, active:{}, core:{}, max:{}, queue:{}]",
                env.getPipelineRunInstanceUuid(), stepRunInstanceUuid,
                executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());
        CompletableFuture.runAsync(() -> {
            try {
                StreamUtil.readStringInputStream(inputStream, 200, Charset.forName("UTF-8"), logConsumer);
            } catch (Exception e) {

            }
        }, executor);

        return future;
    }

    public Response startAndConsumeLog(Consumer<String> logConsumer, Consumer<Exception> exceptionConsumer, ThreadPoolExecutor executor){
        CompletableFuture<Response> future = startAndConsumeLogAsync(logConsumer, exceptionConsumer, executor);

        // get result
        Response res;
        try {
            res = future.get();
        } catch (Exception e) {
            res = new Response().error(e.getMessage());
        }

        return res;
    }

    public String getType(){
        StepRun stepRun = getClass().getAnnotation(StepRun.class);
        if(null != stepRun){
            return stepRun.type();
        }else{
            return null;
        }
    };

    public String getStepRunInstanceUuid(){
        return this.stepRunInstanceUuid;
    }

    public Env getEnv() {
        return env;
    }

    public InputStream getLogInputStream(){
        return this.pipedStream.getInputStream();
    }

    public OutputStream getLogOutputStream(){
        return this.pipedStream.getOutputStream();
    }

    public StreamUtil.PipedStream getPipedStream() {
        return pipedStream;
    }
}