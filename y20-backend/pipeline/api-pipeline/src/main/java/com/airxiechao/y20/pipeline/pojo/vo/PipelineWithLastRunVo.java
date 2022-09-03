package com.airxiechao.y20.pipeline.pojo.vo;

import java.util.Date;

public class PipelineWithLastRunVo {
    private Long id;
    private String name;
    private Boolean bookmarked;
    private String lastStatus;
    private Long lastPipelineRunId;
    private Date lastBeginTime;
    private Date lastEndTime;
    private Boolean flagCronEnabled;
    private Long publishedTemplateId;

    public PipelineWithLastRunVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Long getLastPipelineRunId() {
        return lastPipelineRunId;
    }

    public void setLastPipelineRunId(Long lastPipelineRunId) {
        this.lastPipelineRunId = lastPipelineRunId;
    }

    public Date getLastBeginTime() {
        return lastBeginTime;
    }

    public void setLastBeginTime(Date lastBeginTime) {
        this.lastBeginTime = lastBeginTime;
    }

    public Date getLastEndTime() {
        return lastEndTime;
    }

    public void setLastEndTime(Date lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public Boolean getFlagCronEnabled() {
        return flagCronEnabled;
    }

    public void setFlagCronEnabled(Boolean flagCronEnabled) {
        this.flagCronEnabled = flagCronEnabled;
    }

    public Long getPublishedTemplateId() {
        return publishedTemplateId;
    }

    public void setPublishedTemplateId(Long publishedTemplateId) {
        this.publishedTemplateId = publishedTemplateId;
    }
}
