package com.airxiechao.y20.pipeline.pubsub.event.step;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventStepRunOutputPush extends Event {

    private String pipelineRunInstanceUuid;
    private String stepRunInstanceUuid;
    private String output;

    public static String type(String pipelineRunInstanceUuid, String stepRunInstanceUuid){
        return EnumPipelineEventType.STEP_RUN_OUTPUT_PUSH +"."+pipelineRunInstanceUuid+"."+stepRunInstanceUuid;
    }

    public EventStepRunOutputPush() {
    }

    public EventStepRunOutputPush(String pipelineRunInstanceUuid, String stepRunInstanceUuid, String output){
        setType(type(pipelineRunInstanceUuid, stepRunInstanceUuid));

        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.stepRunInstanceUuid = stepRunInstanceUuid;
        this.output = output;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getStepRunInstanceUuid() {
        return stepRunInstanceUuid;
    }

    public void setStepRunInstanceUuid(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}

