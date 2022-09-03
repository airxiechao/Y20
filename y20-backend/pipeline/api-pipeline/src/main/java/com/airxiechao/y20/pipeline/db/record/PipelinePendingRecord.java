package com.airxiechao.y20.pipeline.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.PipelinePending;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;

@Table("pipeline_pending")
@Index(fields = {"userId", "projectId", "pipelineId", "createTime"})
public class PipelinePendingRecord {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    @Column(length = 100) private String name;
    @Column(type = "text") private String inParams;
    private Boolean flagDebug;
    @Column(length = 100) private String createReason;
    private Date createTime;

    public PipelinePending toPojo(){
        PipelinePending pipelinePending = new PipelinePending();
        pipelinePending.setPipelinePendingId(id);
        pipelinePending.setUserId(userId);
        pipelinePending.setProjectId(projectId);
        pipelinePending.setPipelineId(pipelineId);
        pipelinePending.setName(name);
        pipelinePending.setInParams(JSON.parseObject(inParams, new TypeReference<>(){}));
        pipelinePending.setFlagDebug(flagDebug);
        pipelinePending.setCreateReason(createReason);
        pipelinePending.setCreateTime(createTime);

        return pipelinePending;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInParams() {
        return inParams;
    }

    public void setInParams(String inParams) {
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
