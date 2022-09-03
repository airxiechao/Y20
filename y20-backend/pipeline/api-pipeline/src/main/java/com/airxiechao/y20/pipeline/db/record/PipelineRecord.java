package com.airxiechao.y20.pipeline.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Table("pipeline")
@Index(fields = {"userId", "projectId"})
public class PipelineRecord {

    private Long id;
    private Long userId;
    private Long projectId;
    private Boolean flagOneRun;
    private Boolean flagInjectProjectVariable;
    private Boolean flagCronEnabled;
    private Date cronBeginTime;
    private Integer cronIntervalSecs;
    @Column(type = "text") private String cronInParams;
    @Column(length = 100) private String name;
    @Column(type = "text") private String description;
    @Column(type = "text") private String steps;
    @Column(type = "text") private String variables;
    private Boolean bookmarked;
    private Date bookmarkTime;

    private Long publishedTemplateId;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public Pipeline toPojo(){
        Pipeline pipeline = new Pipeline();
        pipeline.setName(name);
        pipeline.setDescription(description);
        pipeline.setUserId(userId);
        pipeline.setProjectId(projectId);
        pipeline.setPipelineId(id);
        pipeline.setFlagOneRun(flagOneRun);
        pipeline.setFlagInjectProjectVariable(flagInjectProjectVariable);
        pipeline.setFlagCronEnabled(flagCronEnabled);
        pipeline.setCronBeginTime(cronBeginTime);
        pipeline.setCronIntervalSecs(cronIntervalSecs);
        pipeline.setCronInParams(JSON.parseObject(cronInParams, new TypeReference<>(){}));

        List<PipelineStep> steps = JSON.parseArray(this.steps, PipelineStep.class);
        pipeline.setSteps(steps);

        Map<String, PipelineVariable> variables = JSON.parseObject(this.variables,
                new TypeReference<LinkedHashMap<String, PipelineVariable>>(){});
        pipeline.setVariables(variables);

        pipeline.setBookmarked(bookmarked);
        pipeline.setBookmarkTime(bookmarkTime);
        pipeline.setPublishedTemplateId(publishedTemplateId);
        pipeline.setDeleted(deleted);
        pipeline.setCreateTime(createTime);
        pipeline.setDeleteTime(deleteTime);

        return pipeline;
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

    public String getCronInParams() {
        return cronInParams;
    }

    public void setCronInParams(String cronInParams) {
        this.cronInParams = cronInParams;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
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
