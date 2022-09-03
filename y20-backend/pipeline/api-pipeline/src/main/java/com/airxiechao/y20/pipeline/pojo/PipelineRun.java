package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Map;

public class PipelineRun {

    private Long pipelineRunId;
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private String name;
    private Pipeline pipeline;
    private String instanceUuid;
    private String status;
    private Date beginTime;
    private Date endTime;
    private Map<String, String> inParams;
    private Map<String, String> outParams;
    private Boolean flagDebug;
    private String error;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public PipelineRunRecord toRecord(){
        PipelineRunRecord record = new PipelineRunRecord();
        record.setId(pipelineRunId);
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setPipelineId(pipelineId);
        record.setName(name);
        record.setPipeline(JSON.toJSONString(pipeline));
        record.setInstanceUuid(instanceUuid);
        record.setStatus(status);
        record.setBeginTime(beginTime);
        record.setEndTime(endTime);
        record.setInParams(JSON.toJSONString(inParams));
        record.setOutParams(JSON.toJSONString(outParams));
        record.setFlagDebug(flagDebug);
        record.setError(error);
        record.setDeleted(deleted);
        record.setCreateTime(createTime);
        record.setDeleteTime(deleteTime);

        return record;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
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

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String instanceUuid) {
        this.instanceUuid = instanceUuid;
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

    public Boolean getFlagDebug() {
        return flagDebug;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }

    public Map<String, String> getOutParams() {
        return outParams;
    }

    public void setOutParams(Map<String, String> outParams) {
        this.outParams = outParams;
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
