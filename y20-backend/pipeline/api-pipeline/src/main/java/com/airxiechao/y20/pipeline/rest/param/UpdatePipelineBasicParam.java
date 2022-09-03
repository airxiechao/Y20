package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

import java.util.Date;
import java.util.Map;

public class UpdatePipelineBasicParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private String name;
    private String description;
    @Required private Boolean flagOneRun;
    @Required private Boolean flagInjectProjectVariable;
    @Required private Boolean flagCronEnabled;
    @Required(conditionalOnRequiredTrue = "flagCronEnabled") private Date cronBeginTime;
    @Required(conditionalOnRequiredTrue = "flagCronEnabled") private Integer cronIntervalSecs;
    private Map<String, String> cronInParams;

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
}
