package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.y20.pipeline.db.record.PipelineRecord;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class Pipeline {

    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private String name;
    private String description;
    private Boolean flagOneRun;
    private Boolean flagInjectProjectVariable;
    private Boolean flagCronEnabled;
    private Date cronBeginTime;
    private Integer cronIntervalSecs;
    private Map<String, String> cronInParams;
    private List<PipelineStep> steps = new ArrayList<>();
    private Map<String, PipelineVariable> variables = new LinkedHashMap<>();
    private Boolean bookmarked;
    private Date bookmarkTime;

    private Long publishedTemplateId;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public PipelineRecord toRecord(){
        PipelineRecord record = new PipelineRecord();
        record.setId(pipelineId);
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setName(name);
        record.setDescription(description);
        record.setFlagOneRun(flagOneRun);
        record.setFlagInjectProjectVariable(flagInjectProjectVariable);
        record.setFlagCronEnabled(flagCronEnabled);
        record.setCronBeginTime(cronBeginTime);
        record.setCronIntervalSecs(cronIntervalSecs);
        record.setCronInParams(JSON.toJSONString(cronInParams));
        record.setSteps(JSON.toJSONString(steps));
        record.setVariables(JSON.toJSONString(variables));
        record.setBookmarked(bookmarked);
        record.setBookmarkTime(bookmarkTime);
        record.setPublishedTemplateId(publishedTemplateId);
        record.setDeleted(deleted);
        record.setCreateTime(createTime);
        record.setDeleteTime(deleteTime);

        return record;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Pipeline addStep(PipelineStep step){
        steps.add(step);
        return this;
    }

    public void setSteps(List<PipelineStep> steps) {
        this.steps = steps;
    }

    public List<PipelineStep> getSteps() {
        return steps;
    }

    public Map<String, PipelineVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, PipelineVariable> variables) {
        this.variables = variables;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public Date getBookmarkTime() {
        return bookmarkTime;
    }

    public void setBookmarkTime(Date bookmarkTime) {
        this.bookmarkTime = bookmarkTime;
    }

    public Long getPublishedTemplateId() {
        return publishedTemplateId;
    }

    public void setPublishedTemplateId(Long publishedTemplateId) {
        this.publishedTemplateId = publishedTemplateId;
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
