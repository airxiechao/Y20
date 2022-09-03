package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceCopyPipelineFileParam {
    @Required private Long userId;
    @Required private Long fromProjectId;
    @Required private Long fromPipelineId;
    @Required private Long toProjectId;
    @Required private Long toPipelineId;

    public ServiceCopyPipelineFileParam() {
    }

    public ServiceCopyPipelineFileParam(Long userId, Long fromProjectId, Long fromPipelineId, Long toProjectId, Long toPipelineId) {
        this.userId = userId;
        this.fromProjectId = fromProjectId;
        this.fromPipelineId = fromPipelineId;
        this.toProjectId = toProjectId;
        this.toPipelineId = toPipelineId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFromProjectId() {
        return fromProjectId;
    }

    public void setFromProjectId(Long fromProjectId) {
        this.fromProjectId = fromProjectId;
    }

    public Long getFromPipelineId() {
        return fromPipelineId;
    }

    public void setFromPipelineId(Long fromPipelineId) {
        this.fromPipelineId = fromPipelineId;
    }

    public Long getToProjectId() {
        return toProjectId;
    }

    public void setToProjectId(Long toProjectId) {
        this.toProjectId = toProjectId;
    }

    public Long getToPipelineId() {
        return toPipelineId;
    }

    public void setToPipelineId(Long toPipelineId) {
        this.toPipelineId = toPipelineId;
    }
}
