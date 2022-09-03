package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.y20.pipeline.db.record.PipelinePendingRecord;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Map;

public class PipelinePending {
    private Long pipelinePendingId;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private String name;
    private Map<String, String> inParams;
    private Boolean flagDebug;
    private String createReason;
    private Date createTime;

    public PipelinePendingRecord toRecord(){
        PipelinePendingRecord record = new PipelinePendingRecord();
        record.setId(pipelinePendingId);
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setPipelineId(pipelineId);
        record.setName(name);
        record.setInParams(JSON.toJSONString(inParams));
        record.setFlagDebug(flagDebug);
        record.setCreateReason(createReason);
        record.setCreateTime(createTime);

        return record;
    }

    public Long getPipelinePendingId() {
        return pipelinePendingId;
    }

    public void setPipelinePendingId(Long pipelinePendingId) {
        this.pipelinePendingId = pipelinePendingId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }

    public Boolean getFlagDebug() {
        return flagDebug;
    }

    public void setFlagDebug(Boolean flagDebug) {
        this.flagDebug = flagDebug;
    }

    public String getCreateReason() {
        return createReason;
    }

    public void setCreateReason(String createReason) {
        this.createReason = createReason;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
