package com.airxiechao.y20.pipeline.pojo.vo;

import com.airxiechao.y20.pipeline.db.record.PipelineRecord;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;

import java.util.Date;

public class PipelineRunBasicVo {
    private Long pipelineRunId;
    private String name;
    private String status;
    private Date beginTime;
    private Date endTime;

    public PipelineRunBasicVo() {
    }

    public PipelineRunBasicVo(PipelineRunRecord record) {
        this.pipelineRunId = record.getId();
        this.name = record.getName();
        this.status = record.getStatus();
        this.beginTime = record.getBeginTime();
        this.endTime = record.getEndTime();
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
