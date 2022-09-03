package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;

import java.util.List;
import java.util.Map;

public class ServiceCreatePipelineParam {

    @Required private Long userId;
    @Required private Long projectId;
    @Required private String name;
    private String description;
    @Required private Boolean flagOneRun;
    private List<PipelineStep> steps;
    private Map<String, PipelineVariable> variables;

    public ServiceCreatePipelineParam() {
    }

    public ServiceCreatePipelineParam(Long userId, Long projectId, String name, String description, Boolean flagOneRun,
                                      List<PipelineStep> steps, Map<String, PipelineVariable> variables) {
        this.userId = userId;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.flagOneRun = flagOneRun;
        this.steps = steps;
        this.variables = variables;
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
