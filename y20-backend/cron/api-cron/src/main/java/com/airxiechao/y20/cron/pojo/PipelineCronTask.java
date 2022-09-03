package com.airxiechao.y20.cron.pojo;

import java.util.Date;
import java.util.Map;

public class PipelineCronTask {

    private Long pipelineId;
    private Date beginTime;
    private Integer intervalSecs;
    private Map<String, String> inParams;

    public PipelineCronTask(Long pipelineId, Date beginTime, Integer intervalSecs, Map<String, String> inParams) {
        this.pipelineId = pipelineId;
        this.beginTime = beginTime;
        this.intervalSecs = intervalSecs;
        this.inParams = inParams;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getIntervalSecs() {
        return intervalSecs;
    }

    public void setIntervalSecs(Integer intervalSecs) {
        this.intervalSecs = intervalSecs;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }
}
