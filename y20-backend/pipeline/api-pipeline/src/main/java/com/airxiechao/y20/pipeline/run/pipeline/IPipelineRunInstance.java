package com.airxiechao.y20.pipeline.run.pipeline;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.function.QuadConsumer;
import com.airxiechao.axcboot.util.function.TriConsumer;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunTransferProgressListener;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.terminal.ITerminalRunInstance;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IPipelineRunInstance {
    void start();
    CompletableFuture startAsync();
    void iterateStep();
    CompletableFuture iterateStepAsync();
    void nextStep();
    CompletableFuture nextStepAsync();
    void stop();
    CompletableFuture stopAsync();

    void stepRunInstanceCallback(String stepRunInstanceUuid, Response response);
    String getPipelineRunInstanceUuid();

    boolean isStopped();
    Env getCurrentEnv();

    // pipeline run
    void addPipelineRunStatusUpdateListener(QuadConsumer<String, String, String, Map<String, String>> pipelineRunStatusUpdateListener);
    void removePipelineRunStatusUpdateListener(QuadConsumer<String, String, String, Map<String, String>> pipelineRunStatusUpdateListener);

    // step run
    AbstractStepRunInstance getStepRunInstance(String stepRunUuid);
    void addStepRunOutputListener(BiConsumer<Integer, String> stepRunLogListener);
    void removeStepRunOutputListener(BiConsumer<Integer, String> stepRunLogListener);
    void addStepRunStatusUpdateListener(QuadConsumer<Integer, String, String, String> stepRunStatusUpdateListener);
    void removeStepRunStatusUpdateListener(QuadConsumer<Integer, String, String, String> stepRunStatusUpdateListener);

    // explorer run
    IExplorerRunInstance getExplorerRunInstance(String explorerRunInstanceUuid);
    IExplorerRunInstance createExplorerRunInstance() throws Exception;
    void destroyExplorerRunInstance(String explorerRunInstanceUuid) throws Exception;
    void addExplorerRunStatusUpdateListener(BiConsumer<String, String> explorerRunStatusUpdateListener);
    void removeExplorerRunStatusUpdateListener(BiConsumer<String, String> explorerRunStatusUpdateListener);
    void addExplorerRunTransferProgressListener(IExplorerRunTransferProgressListener explorerRunTransferProgressListener);
    void removeExplorerRunTransferProgressListener(IExplorerRunTransferProgressListener explorerRunTransferProgressListener);

    // terminal run
    ITerminalRunInstance getTerminalRunInstance(String terminalRunInstanceUuid);
    ITerminalRunInstance createTerminalRunInstance(String terminalType) throws Exception;
    void destroyTerminalRunInstance(String terminalRunInstanceUuid) throws Exception;
    void addTerminalRunStatusUpdateListener(BiConsumer<String, String> terminalRunStatusUpdateListener);
    void removeTerminalRunStatusUpdateListener(BiConsumer<String, String> terminalRunStatusUpdateListener);
    void addTerminalRunOutputListener(BiConsumer<String, String> terminalRunOutputListener);
    void removeTerminalRunOutputListener(BiConsumer<String, String> terminalRunStatusListener);
}
