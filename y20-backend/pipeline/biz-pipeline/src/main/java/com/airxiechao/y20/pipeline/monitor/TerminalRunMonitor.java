package com.airxiechao.y20.pipeline.monitor;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TerminalRunMonitor {
    private Long pipelineRunId;
    private String terminalRunInstanceUuid;
    private String monitorUuid;

    private List<Consumer<String>> terminalRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<Consumer<String>> terminalRunOutputListeners = Collections.synchronizedList(new ArrayList<>());

    public TerminalRunMonitor(Long pipelineRunId, String terminalRunInstanceUuid) {
        this.pipelineRunId = pipelineRunId;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.monitorUuid = "monitor-terminal-run-" + UuidUtil.random();
    }

    public void start(){
        // terminal run status
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineTerminalRunStatusUpdate.type(this.pipelineRunId, this.terminalRunInstanceUuid), monitorUuid, map -> {
                    EventPipelineTerminalRunStatusUpdate event = ModelUtil.fromMap(map, EventPipelineTerminalRunStatusUpdate.class);
                    this.terminalRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(event.getStatus());
                    });

                    return new Response();
                });

        // terminal run output
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineTerminalRunOutput.type(this.pipelineRunId, this.terminalRunInstanceUuid), monitorUuid, map -> {
                    EventPipelineTerminalRunOutput event = ModelUtil.fromMap(map, EventPipelineTerminalRunOutput.class);
                    this.terminalRunOutputListeners.forEach(listener -> {
                        listener.accept(event.getOutput());
                    });

                    return new Response();
                });
    }

    public void stop(){
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineTerminalRunStatusUpdate.type(this.pipelineRunId, this.terminalRunInstanceUuid), monitorUuid);
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineTerminalRunOutput.type(this.pipelineRunId, this.terminalRunInstanceUuid), monitorUuid);
    }

    public void addTerminalRunStatusUpdateListener(Consumer<String> terminalRunStatusListener){
        this.terminalRunStatusUpdateListeners.add(terminalRunStatusListener);
    }

    public void addTerminalRunOutputListener(Consumer<String> terminalRunLogListener){
        this.terminalRunOutputListeners.add(terminalRunLogListener);
    }
}
