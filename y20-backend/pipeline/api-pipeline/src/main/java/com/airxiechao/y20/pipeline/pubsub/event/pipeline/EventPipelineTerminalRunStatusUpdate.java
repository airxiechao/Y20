package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventPipelineTerminalRunStatusUpdate extends Event {

    public static String type(Long pipelineRunId, String terminalRunInstanceUuid){
        if(null == terminalRunInstanceUuid){
            terminalRunInstanceUuid = "*";
        }
        return EnumPipelineEventType.PIPELINE_TERMINAL_RUN_STATUS_UPDATE +"."+pipelineRunId+"."+terminalRunInstanceUuid;
    }

    private Long pipelineRunId;
    private String terminalRunInstanceUuid;
    private String status;

    public EventPipelineTerminalRunStatusUpdate() {
    }

    public EventPipelineTerminalRunStatusUpdate(Long pipelineRunId, String terminalRunInstanceUuid, String status){
        setType(type(pipelineRunId, terminalRunInstanceUuid));

        this.pipelineRunId = pipelineRunId;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.status = status;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
