package com.airxiechao.y20.pipeline.pubsub.event.pipeline;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

import java.util.Date;

public class EventPipelineRunActivity extends Event {
    public static String type(Long pipelineRunId){
        return EnumPipelineEventType.PIPELINE_RUN_ACTIVITY+"."+(null != pipelineRunId ? pipelineRunId : "*");
    }

    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private String pipelineName;
    private Long pipelineRunId;
    private String pipelineRunName;
    private String status;
    private String error;
    private Date beginTime;
    private Date endTime;

    public EventPipelineRunActivity() {
    }

    public EventPipelineRunActivity(
            Long userId,
            Long projectId,
            Long pipelineId,
            String pipelineName,
            Long pipelineRunId,
            String pipelineRunName,
            String status,
            String error,
            Date beginTime,
            Date endTime
    ) {
        setType(type(pipelineRunId));

        this.userId = userId;
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.pipelineName = pipelineName;
        this.pipelineRunId = pipelineRunId;
        this.pipelineRunName = pipelineRunName;
        this.status = status;
        this.error = error;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public String getPipelineRunName() {
        return pipelineRunName;
    }

    public void setPipelineRunName(String pipelineRunName) {
        this.pipelineRunName = pipelineRunName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
