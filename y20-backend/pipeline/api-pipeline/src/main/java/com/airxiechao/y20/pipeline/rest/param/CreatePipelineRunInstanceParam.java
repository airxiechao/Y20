package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CreatePipelineRunInstanceParam {

    @Required private Long pipelineRunId;

    public CreatePipelineRunInstanceParam() {
    }

    public CreatePipelineRunInstanceParam(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }
}
