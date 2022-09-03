package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventPipelineStepRunStatusUpdate extends Event {

    public static String type(Long pipelineRunId, Integer position){
        String strPosition = null == position ? "*" : position.toString();
        return EnumPipelineEventType.PIPELINE_STEP_RUN_STATUS_UPDATE+"."+pipelineRunId+"."+strPosition;
    }

    private Long pipelineRunId;
    private Integer position;
    private String status;
    private String error;

    public EventPipelineStepRunStatusUpdate() {
    }

    public EventPipelineStepRunStatusUpdate(Long pipelineRunId, Integer position, String status, String error) {
        setType(type(pipelineRunId, position));

        this.pipelineRunId = pipelineRunId;
        this.position = position;
        this.status = status;
        this.error = error;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
