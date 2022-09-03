package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventPipelineExplorerRunStatusUpdate extends Event {

    public static String type(Long pipelineRunId, String explorerRunInstanceUuid){
        if(null == explorerRunInstanceUuid){
            explorerRunInstanceUuid = "*";
        }
        return EnumPipelineEventType.PIPELINE_EXPLORER_RUN_STATUS_UPDATE +"."+pipelineRunId+"."+explorerRunInstanceUuid;
    }

    private Long pipelineRunId;
    private String explorerRunInstanceUuid;
    private String status;

    public EventPipelineExplorerRunStatusUpdate() {
    }

    public EventPipelineExplorerRunStatusUpdate(Long pipelineRunId, String explorerRunInstanceUuid, String status){
        setType(type(pipelineRunId, explorerRunInstanceUuid));

        this.pipelineRunId = pipelineRunId;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.status = status;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}