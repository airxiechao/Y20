package com.airxiechao.y20.template.pojo;

import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class Template {
    private Long templateId;
    private Long userId;
    private String username;
    private String name;
    private String description;
    private List<PipelineStep> steps = new ArrayList<>();
    private Map<String, PipelineVariable> variables = new LinkedHashMap<>();
    private Integer numApply;
    private Date createTime;
    private Date lastUpdateTime;

    public TemplateRecord toRecord(){
        TemplateRecord record = new TemplateRecord();
        record.setId(templateId);
        record.setUserId(userId);
        record.setUsername(username);
        record.setName(name);
        record.setDescription(description);
        record.setSteps(JSON.toJSONString(steps));
        record.setVariables(JSON.toJSONString(variables));
        record.setNumApply(numApply);
        record.setCreateTime(createTime);
        record.setLastUpdateTime(lastUpdateTime);

        return record;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<PipelineStep> getSteps() {
        return steps;
    }

    public void setSteps(List<PipelineStep> steps) {
        this.steps = steps;
    }

    public Map<String, PipelineVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, PipelineVariable> variables) {
        this.variables = variables;
    }

    public Integer getNumApply() {
        return numApply;
    }

    public void setNumApply(Integer numApply) {
        this.numApply = numApply;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
