package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceListScheduledPipelineParam {

    @Required private Long fromPipelineId;
    @Required private Long toPipelineId;

    public ServiceListScheduledPipelineParam() {
    }

    public ServiceListScheduledPipelineParam(Long fromPipelineId, Long toPipelineId) {
        this.fromPipelineId = fromPipelineId;
        this.toPipelineId = toPipelineId;
    }

    public Long getFromPipelineId() {
        return fromPipelineId;
    }

    public void setFromPipelineId(Long fromPipelineId) {
        this.fromPipelineId = fromPipelineId;
    }

    public Long getToPipelineId() {
        return toPipelineId;
    }

    public void setToPipelineId(Long toPipelineId) {
        this.toPipelineId = toPipelineId;
    }
}
