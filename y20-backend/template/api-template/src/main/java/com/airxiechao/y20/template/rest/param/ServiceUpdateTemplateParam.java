package com.airxiechao.y20.template.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;

import java.util.List;
import java.util.Map;

public class ServiceUpdateTemplateParam {
    @Required private String name;
    private String description;
    @Required private Long userId;
    @Required private String username;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private Long templateId;
    private List<PipelineStep> steps;
    private Map<String, PipelineVariable> variables;

    public ServiceUpdateTemplateParam() {
    }

    public ServiceUpdateTemplateParam(String name, String description, Long userId, String username, Long projectId, Long pipelineId, Long templateId, List<PipelineStep> steps, Map<String, PipelineVariable> variables) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.username = username;
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.templateId = templateId;
        this.steps = steps;
        this.variables = variables;
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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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
}
