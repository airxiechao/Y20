package com.airxiechao.y20.pipeline.rest.ws.session;

public class TerminalRunWsSession {
    private Long pipelineRunId;
    private String pipelineRunInstanceUuid;
    private String terminalRunInstanceUuid;
    private String listenerId;

    public TerminalRunWsSession(Long pipelineRunId, String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String listenerId) {
        this.pipelineRunId = pipelineRunId;
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.listenerId = listenerId;
    }

    public String getName(){
        return pipelineRunId+"."+terminalRunInstanceUuid+":"+listenerId;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
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

    public String getListenerId() {
        return listenerId;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }
}
