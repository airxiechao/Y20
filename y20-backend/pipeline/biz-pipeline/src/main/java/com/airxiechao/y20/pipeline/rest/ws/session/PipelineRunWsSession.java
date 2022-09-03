package com.airxiechao.y20.pipeline.rest.ws.session;

public class PipelineRunWsSession {
    private Long pipelineRunId;
    private String listenerId;

    public PipelineRunWsSession(Long pipelineRunId, String listenerId) {
        this.pipelineRunId = pipelineRunId;
        this.listenerId = listenerId;
    }

    public String getName(){
        return pipelineRunId +":"+listenerId;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public String getListenerId() {
        return listenerId;
    }
}
