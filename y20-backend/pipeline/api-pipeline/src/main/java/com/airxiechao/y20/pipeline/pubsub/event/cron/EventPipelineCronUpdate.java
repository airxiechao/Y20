package com.airxiechao.y20.pipeline.pubsub.event.cron;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

import java.util.Date;
import java.util.Map;

public class EventPipelineCronUpdate extends Event {
    public static String type(Long pipelineId){
        return EnumPipelineEventType.PIPELINE_CRON_UPDATE + "." + (null != pipelineId ? pipelineId : "*");
    }

    private Long pipelineId;
    private Boolean flagEnabled;
    private Date beginTime;
    private Integer intervalSecs;
    private Map<String, String> inParams;

    public EventPipelineCronUpdate() {
    }

    public EventPipelineCronUpdate(Long pipelineId, Boolean flagEnabled, Date beginTime, Integer intervalSecs, Map<String, String> inParams){
        setType(type(pipelineId));

        this.pipelineId = pipelineId;
        this.flagEnabled = flagEnabled;
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

    public Boolean getFlagEnabled() {
        return flagEnabled;
    }

    public void setFlagEnabled(Boolean flagEnabled) {
        this.flagEnabled = flagEnabled;
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
