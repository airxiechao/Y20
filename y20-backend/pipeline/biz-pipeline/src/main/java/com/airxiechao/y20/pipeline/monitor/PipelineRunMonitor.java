package com.airxiechao.y20.pipeline.monitor;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PipelineRunMonitor {

    private Long pipelineRunId;
    private String monitorUuid;

    private List<Consumer<String>> pipelineRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<Integer, String>> stepRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());
    private List<BiConsumer<Integer, String>> stepRunOutputListeners = Collections.synchronizedList(new ArrayList<>());

    public PipelineRunMonitor(Long pipelineRunId){
        this.pipelineRunId = pipelineRunId;
        this.monitorUuid = "monitor-pipeline-run-" + UuidUtil.random();
    }

    public void start(){
        // pipeline run status
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineRunStatusUpdate.type(this.pipelineRunId), monitorUuid, map -> {
                    EventPipelineRunStatusUpdate event = ModelUtil.fromMap(map, EventPipelineRunStatusUpdate.class);
                    this.pipelineRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(event.getStatus());
                    });

                    return new Response();
                });

        // step run status
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineStepRunStatusUpdate.type(this.pipelineRunId, null), monitorUuid, map -> {
                    EventPipelineStepRunStatusUpdate event = ModelUtil.fromMap(map, EventPipelineStepRunStatusUpdate.class);
                    this.stepRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(event.getPosition(), event.getStatus());
                    });

                    return new Response();
                });

        // step run output
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineStepRunOutput.type(this.pipelineRunId, null), monitorUuid, map -> {
                    EventPipelineStepRunOutput event = ModelUtil.fromMap(map, EventPipelineStepRunOutput.class);
                    this.stepRunOutputListeners.forEach(listener -> {
                        listener.accept(event.getPosition(), event.getOutput());
                    });

                    return new Response();
                });
    }

    public void stop(){
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineRunStatusUpdate.type(this.pipelineRunId), monitorUuid);
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineStepRunStatusUpdate.type(this.pipelineRunId, null), monitorUuid);
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineStepRunOutput.type(this.pipelineRunId, null), monitorUuid);
    }

    public void addPipelineRunStatusUpdateListener(Consumer<String> pipelineRunStatusListener){
        this.pipelineRunStatusUpdateListeners.add(pipelineRunStatusListener);
    }

    public void addStepRunStatusUpdateListener(BiConsumer<Integer, String> stepRunStatusListener){
        this.stepRunStatusUpdateListeners.add(stepRunStatusListener);
    }

    public void addStepRunOutputListener(BiConsumer<Integer, String> stepRunLogListener){
        this.stepRunOutputListeners.add(stepRunLogListener);
    }
}
