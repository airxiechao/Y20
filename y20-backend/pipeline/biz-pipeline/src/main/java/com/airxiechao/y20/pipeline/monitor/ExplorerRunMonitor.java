package com.airxiechao.y20.pipeline.monitor;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.EventPipelineExplorerRunStatusUpdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExplorerRunMonitor {

    private Long pipelineRunId;
    private String explorerRunInstanceUuid;
    private String monitorUuid;

    private List<Consumer<String>> explorerRunStatusUpdateListeners = Collections.synchronizedList(new ArrayList<>());

    public ExplorerRunMonitor(Long pipelineRunId, String explorerRunInstanceUuid) {
        this.pipelineRunId = pipelineRunId;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.monitorUuid = "monitor-explorer-run-" + UuidUtil.random();
    }

    public void start(){
        // explorer run status
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineExplorerRunStatusUpdate.type(this.pipelineRunId, this.explorerRunInstanceUuid), monitorUuid, map -> {
                    EventPipelineExplorerRunStatusUpdate event = ModelUtil.fromMap(map, EventPipelineExplorerRunStatusUpdate.class);
                    this.explorerRunStatusUpdateListeners.forEach(listener -> {
                        listener.accept(event.getStatus());
                    });

                    return new Response();
                });
    }

    public void stop(){
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineExplorerRunStatusUpdate.type(this.pipelineRunId, this.explorerRunInstanceUuid), monitorUuid);
    }

    public void addExplorerRunStatusUpdateListener(Consumer<String> explorerRunStatusListener){
        this.explorerRunStatusUpdateListeners.add(explorerRunStatusListener);
    }

}
