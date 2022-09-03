package com.airxiechao.y20.pipeline.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;
import java.util.Map;

@Table("pipeline_run")
@Index(fields = {"userId", "projectId", "pipelineId"})
@Index(fields = {"userId", "status"})
public class PipelineRunRecord {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    @Column(length = 100) private String name;
    @Column(type = "text") private String pipeline;
    private String instanceUuid;
    private String status;
    private Date beginTime;
    private Date endTime;
    @Column(type = "text") private String inParams;
    @Column(type = "text") private String outParams;
    private Boolean flagDebug;
    @Column(length = 1000) private String error;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public PipelineRun toPojo(){
        PipelineRun pipelineRun = new PipelineRun();
        pipelineRun.setPipelineRunId(id);
        pipelineRun.setUserId(userId);
        pipelineRun.setProjectId(projectId);
        pipelineRun.setPipelineId(pipelineId);
        pipelineRun.setName(name);
        pipelineRun.setPipeline(JSON.parseObject(pipeline, Pipeline.class));
        pipelineRun.setInstanceUuid(instanceUuid);
        pipelineRun.setStatus(status);
        pipelineRun.setBeginTime(beginTime);
        pipelineRun.setEndTime(endTime);
        pipelineRun.setInParams(JSON.parseObject(inParams, new TypeReference<>(){}));
        pipelineRun.setOutParams(JSON.parseObject(outParams, new TypeReference<>(){}));
        pipelineRun.setFlagDebug(flagDebug);
        pipelineRun.setError(error);
        pipelineRun.setDeleted(deleted);
        pipelineRun.setCreateTime(createTime);
        pipelineRun.setDeleteTime(deleteTime);

        return pipelineRun;
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

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String runInstanceId) {
        this.instanceUuid = runInstanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getInParams() {
        return inParams;
    }

    public void setInParams(String inParams) {
        this.inParams = inParams;
    }

    public String getOutParams() {
        return outParams;
    }

    public void setOutParams(String outParams) {
        this.outParams = outParams;
    }

    public Boolean getFlagDebug() {
        return flagDebug;
    }

    public void setFlagDebug(Boolean flagDebug) {
        this.flagDebug = flagDebug;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}
