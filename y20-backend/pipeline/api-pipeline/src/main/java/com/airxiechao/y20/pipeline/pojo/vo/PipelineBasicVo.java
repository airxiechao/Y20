package com.airxiechao.y20.pipeline.pojo.vo;

import com.airxiechao.y20.pipeline.db.record.PipelineRecord;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;

import java.util.Date;
import java.util.Map;

public class PipelineBasicVo {
    private Long pipelineId;
    private String name;
    private String description;
    private Boolean flagOneRun;
    private Boolean flagInjectProjectVariable;
    private Boolean flagCronEnabled;
    private Date cronBeginTime;
    private Integer cronIntervalSecs;
    private Map<String, String> cronInParams;
    private Long publishedTemplateId;

    public PipelineBasicVo(){

    }

    public PipelineBasicVo(PipelineRecord pipelineRecord){
        Pipeline pipeline = pipelineRecord.toPojo();

        this.pipelineId = pipeline.getPipelineId();
        this.name = pipeline.getName();
        this.description = pipeline.getDescription();
        this.flagOneRun = pipeline.getFlagOneRun();
        this.flagInjectProjectVariable = pipeline.getFlagInjectProjectVariable();
        this.flagCronEnabled = pipeline.getFlagCronEnabled();
        this.cronBeginTime = pipeline.getCronBeginTime();
        this.cronIntervalSecs = pipeline.getCronIntervalSecs();
        this.cronInParams = pipeline.getCronInParams();
        this.publishedTemplateId = pipeline.getPublishedTemplateId();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFlagOneRun() {
        return flagOneRun;
    }

    public void setFlagOneRun(Boolean flagOneRun) {
        this.flagOneRun = flagOneRun;
    }

    public Boolean getFlagInjectProjectVariable() {
        return flagInjectProjectVariable;
    }

    public void setFlagInjectProjectVariable(Boolean flagInjectProjectVariable) {
        this.flagInjectProjectVariable = flagInjectProjectVariable;
    }

    public Boolean getFlagCronEnabled() {
        return flagCronEnabled;
    }

    public void setFlagCronEnabled(Boolean flagCronEnabled) {
        this.flagCronEnabled = flagCronEnabled;
    }

    public Date getCronBeginTime() {
        return cronBeginTime;
    }

    public void setCronBeginTime(Date cronBeginTime) {
        this.cronBeginTime = cronBeginTime;
    }

    public Integer getCronIntervalSecs() {
        return cronIntervalSecs;
    }

    public void setCronIntervalSecs(Integer cronIntervalSecs) {
        this.cronIntervalSecs = cronIntervalSecs;
    }

    public Map<String, String> getCronInParams() {
        return cronInParams;
    }

    public void setCronInParams(Map<String, String> cronInParams) {
        this.cronInParams = cronInParams;
    }

    public Long getPublishedTemplateId() {
        return publishedTemplateId;
    }

    public void setPublishedTemplateId(Long publishedTemplateId) {
        this.publishedTemplateId = publishedTemplateId;
    }
}
