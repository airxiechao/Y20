package com.airxiechao.y20.pipeline.pubsub.event.terminal;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventTerminalRunOutputPush extends Event {

    private String pipelineRunInstanceUuid;
    private String terminalRunInstanceUuid;
    private String output;

    public static String type(String pipelineRunInstanceUuid, String terminalRunInstanceUuid){
        return EnumPipelineEventType.TERMINAL_RUN_OUTPUT_PUSH+"."+pipelineRunInstanceUuid+"."+terminalRunInstanceUuid;
    }

    public EventTerminalRunOutputPush() {
    }

    public EventTerminalRunOutputPush(String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String output){
        setType(type(pipelineRunInstanceUuid, terminalRunInstanceUuid));

        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.output = output;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
