package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.y20.pipeline.db.record.PipelineWebhookTriggerRecord;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Map;

public class PipelineWebhookTrigger {

    private Long pipelineWebhookTriggerId;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private String sourceType;
    private String eventType;
    private String tag;
    private Map<String, String> inParams;
    private Date createTime;
    private Date lastTriggerTime;
    private Long lastTriggerPipelineRunId;

    public PipelineWebhookTriggerRecord toRecord(){
        PipelineWebhookTriggerRecord record = new PipelineWebhookTriggerRecord();
        record.setId(pipelineWebhookTriggerId);
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setPipelineId(pipelineId);
        record.setSourceType(sourceType);
        record.setEventType(eventType);
        record.setTag(tag);
        record.setInParams(JSON.toJSONString(inParams));
        record.setCreateTime(createTime);
        record.setLastTriggerTime(lastTriggerTime);
        record.setLastTriggerPipelineRunId(lastTriggerPipelineRunId);

        return record;
    }

    public Long getPipelineWebhookTriggerId() {
        return pipelineWebhookTriggerId;
    }

    public void setPipelineWebhookTriggerId(Long pipelineWebhookTriggerId) {
        this.pipelineWebhookTriggerId = pipelineWebhookTriggerId;
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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTriggerTime() {
        return lastTriggerTime;
    }

    public void setLastTriggerTime(Date lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public Long getLastTriggerPipelineRunId() {
        return lastTriggerPipelineRunId;
    }

    public void setLastTriggerPipelineRunId(Long lastTriggerPipelineRunId) {
        this.lastTriggerPipelineRunId = lastTriggerPipelineRunId;
    }
}
