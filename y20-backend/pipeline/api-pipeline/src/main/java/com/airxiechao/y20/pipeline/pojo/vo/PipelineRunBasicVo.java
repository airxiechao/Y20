package com.airxiechao.y20.pipeline.pojo.vo;

import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;

import java.util.Date;

public class PipelineRunBasicVo {
    private Long projectId;
    private Long pipelineId;
    private String pipelineName;
    private Long pipelineRunId;
    private String name;
    private String status;
    private Date beginTime;
    private Date endTime;

    public PipelineRunBasicVo() {
    }

    public PipelineRunBasicVo(PipelineRunRecord record) {
        PipelineRun pipelineRun = record.toPojo();

        this.projectId = pipelineRun.getProjectId();
        this.pipelineId = pipelineRun.getPipelineId();
        this.pipelineName = pipelineRun.getPipeline().getName();
        this.pipelineRunId = pipelineRun.getPipelineRunId();
        this.name = pipelineRun.getName();
        this.status = pipelineRun.getStatus();
        this.beginTime = pipelineRun.getBeginTime();
        this.endTime = pipelineRun.getEndTime();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
