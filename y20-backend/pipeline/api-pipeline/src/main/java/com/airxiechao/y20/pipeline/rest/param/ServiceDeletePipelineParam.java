package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceDeletePipelineParam {
    @Required private Long pipelineId;

    public ServiceDeletePipelineParam() {
    }

    public ServiceDeletePipelineParam(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }
}
