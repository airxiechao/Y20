package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventPipelineTerminalRunOutput extends Event {

    public static String type(Long pipelineRunId, String terminalRunInstanceUuid){
        return EnumPipelineEventType.PIPELINE_TERMINAL_RUN_OUTPUT +"."+pipelineRunId+"."+terminalRunInstanceUuid;
    }

    private Long pipelineRunId;
    private String terminalRunInstanceUuid;
    private String output;

    public EventPipelineTerminalRunOutput() {
    }

    public EventPipelineTerminalRunOutput(Long pipelineRunId, String terminalRunInstanceUuid, String output){
        setType(type(pipelineRunId, terminalRunInstanceUuid));

        this.pipelineRunId = pipelineRunId;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.output = output;
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

}
