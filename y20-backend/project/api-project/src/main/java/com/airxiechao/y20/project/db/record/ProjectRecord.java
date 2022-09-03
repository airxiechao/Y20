package com.airxiechao.y20.project.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.project.pojo.Project;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;
import java.util.LinkedHashMap;

@Table(value = "project")
public class ProjectRecord {
    private Long id;
    private Long userId;
    @Column(length = 500) private String name;
    @Column(type = "text") private String variables;
    private Boolean bookmarked;
    private Date bookmarkTime;
    private Boolean deleted;
    private Date createTime;
    private Date deleteTime;

    public Project toPojo(){
        Project project = new Project();
        project.setUserId(userId);
        project.setProjectId(id);
        project.setName(name);
        project.setVariables(JSON.parseObject(variables, new TypeReference<LinkedHashMap<String, PipelineVariable>>(){}));
        project.setBookmarked(bookmarked);
        project.setBookmarkTime(bookmarkTime);
        project.setDeleted(deleted);
        project.setCreateTime(createTime);
        project.setDeleteTime(deleteTime);

        return project;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
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
