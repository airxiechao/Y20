package com.airxiechao.y20.project.pojo.vo;

import com.airxiechao.y20.project.db.record.ProjectRecord;

public class ProjectDetailVo {
    private Long userId;
    private Long projectId;
    private String name;
    private Boolean bookmarked;
    private Long numPipeline;
    private Long numRunning;

    public ProjectDetailVo() {
    }

    public ProjectDetailVo(ProjectRecord record, Long numPipeline, Long numRunning){
        this.userId = record.getUserId();
        this.projectId = record.getId();
        this.name = record.getName();
        this.bookmarked = record.getBookmarked();
        this.numPipeline = numPipeline;
        this.numRunning = numRunning;
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

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public Long getNumPipeline() {
        return numPipeline;
    }

    public void setNumPipeline(Long numPipeline) {
        this.numPipeline = numPipeline;
    }

    public Long getNumRunning() {
        return numRunning;
    }

    public void setNumRunning(Long numRunning) {
        this.numRunning = numRunning;
    }
}
