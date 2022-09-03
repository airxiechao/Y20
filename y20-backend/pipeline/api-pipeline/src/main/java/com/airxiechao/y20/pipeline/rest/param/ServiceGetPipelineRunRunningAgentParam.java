package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceGetPipelineRunRunningAgentParam {
    @Required private Long userId;

    public ServiceGetPipelineRunRunningAgentParam() {
    }

    public ServiceGetPipelineRunRunningAgentParam(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
