package com.airxiechao.y20.pipeline.pubsub.event.pipeline;


import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

import java.util.Map;

public class EventPipelineRunStatusUpdate extends Event {

    public static String type(Long pipelineRunId){
        return EnumPipelineEventType.PIPELINE_RUN_STATUS_UPDATE+"."+pipelineRunId;
    }

    private Long pipelineRunId;
    private String status;
    private String error;
    private Map<String, String> outParams;

    public EventPipelineRunStatusUpdate() {
    }

    public EventPipelineRunStatusUpdate(Long pipelineRunId, String status, String error, Map<String, String> outParams){
        setType(type(pipelineRunId));

        this.pipelineRunId = pipelineRunId;
        this.status = status;
        this.error = error;
        this.outParams = outParams;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
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

    public Map<String, String> getOutParams() {
        return outParams;
    }

    public void setOutParams(Map<String, String> outParams) {
        this.outParams = outParams;
    }
}
