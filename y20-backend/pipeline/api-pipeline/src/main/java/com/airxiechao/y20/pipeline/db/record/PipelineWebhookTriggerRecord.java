package com.airxiechao.y20.pipeline.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.PipelineWebhookTrigger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;

@Table("pipeline_webhook_trigger")
@Index(fields = {"userId", "tag"})
public class PipelineWebhookTriggerRecord {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    @Column(length = 20) private String sourceType;
    @Column(length = 20) private String eventType;
    @Column(length = 100) private String tag;
    @Column(type = "text") private String inParams;
    private Date createTime;
    private Date lastTriggerTime;
    private Long lastTriggerPipelineRunId;

    public PipelineWebhookTrigger toPojo(){
        PipelineWebhookTrigger trigger = new PipelineWebhookTrigger();
        trigger.setPipelineWebhookTriggerId(id);
        trigger.setUserId(userId);
        trigger.setProjectId(projectId);
        trigger.setPipelineId(pipelineId);
        trigger.setSourceType(sourceType);
        trigger.setEventType(eventType);
        trigger.setTag(tag);
        trigger.setInParams(JSON.parseObject(inParams, new TypeReference<>(){}));
        trigger.setCreateTime(createTime);
        trigger.setLastTriggerTime(lastTriggerTime);
        trigger.setLastTriggerPipelineRunId(lastTriggerPipelineRunId);

        return trigger;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInParams() {
        return inParams;
    }

    public void setInParams(String inParams) {
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
