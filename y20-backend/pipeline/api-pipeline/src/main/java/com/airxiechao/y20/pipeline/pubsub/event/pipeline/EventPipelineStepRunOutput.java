package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventPipelineStepRunOutput extends Event {

    public static String type(Long pipelineRunId, Integer position){
        String strPosition = null == position ? "*" : position.toString();
        return EnumPipelineEventType.PIPELINE_STEP_RUN_OUTPUT +"."+pipelineRunId+"."+strPosition;
    }

    private Long pipelineRunId;
    private Integer position;
    private String output;

    public EventPipelineStepRunOutput() {
    }

    public EventPipelineStepRunOutput(Long pipelineRunId, Integer position, String output){
        setType(type(pipelineRunId, position));

        this.pipelineRunId = pipelineRunId;
        this.position = position;
        this.output = output;
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
