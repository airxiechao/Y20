package com.airxiechao.y20.project.pojo;

import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.project.db.record.ProjectRecord;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Map;

public class Project {
    private Long userId;
    private Long projectId;
    private String name;
    private Map<String, PipelineVariable> variables;
    private Boolean bookmarked;
    private Date bookmarkTime;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public ProjectRecord toRecord(){
        ProjectRecord projectRecord = new ProjectRecord();
        projectRecord.setId(projectId);
        projectRecord.setUserId(userId);
        projectRecord.setName(name);
        projectRecord.setVariables(JSON.toJSONString(variables));
        projectRecord.setBookmarked(bookmarked);
        projectRecord.setBookmarkTime(bookmarkTime);
        projectRecord.setDeleted(deleted);
        projectRecord.setCreateTime(createTime);
        projectRecord.setDeleteTime(deleteTime);

        return projectRecord;
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

    public Map<String, PipelineVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, PipelineVariable> variables) {
        this.variables = variables;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
