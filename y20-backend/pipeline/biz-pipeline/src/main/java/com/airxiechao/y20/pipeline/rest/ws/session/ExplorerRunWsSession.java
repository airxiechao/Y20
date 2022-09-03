package com.airxiechao.y20.pipeline.rest.ws.session;

public class ExplorerRunWsSession {
    private Long pipelineRunId;
    private String pipelineRunInstanceUuid;
    private String explorerRunInstanceUuid;
    private String listenerId;

    public ExplorerRunWsSession(Long pipelineRunId, String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String listenerId) {
        this.pipelineRunId = pipelineRunId;
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.listenerId = listenerId;
    }

    public String getName(){
        return pipelineRunId+"."+explorerRunInstanceUuid+":"+listenerId;
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

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getListenerId() {
        return listenerId;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }
}
