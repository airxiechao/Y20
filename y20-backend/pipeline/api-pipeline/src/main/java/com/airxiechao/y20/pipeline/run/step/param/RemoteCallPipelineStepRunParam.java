package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

import java.util.Map;

public class RemoteCallPipelineStepRunParam {

    @StepTypeParam(
            displayOrder = 1,
            displayName = "项目",
            type = EnumPipelineParamType.SELECT_PROJECT)
    @Required
    private Long projectId;

    @StepTypeParam(
            displayOrder = 2,
            displayName = "流水线",
            type = EnumPipelineParamType.SELECT_PIPELINE)
    private Long pipelineId;

    @StepTypeParam(
            displayOrder = 3,
            displayName = "输入变量",
            type = EnumPipelineParamType.INPUT_PIPELINE_IN_PARAMS)
    private Map<String, String> inParams;

    @StepTypeParam(
            displayOrder = 4,
            displayName = "输出变量前缀",
            hint = "被调用流水线的输出变量将以添加前缀的方式作为步骤输出",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String outParamPrefix;

    public RemoteCallPipelineStepRunParam() {
    }

    public RemoteCallPipelineStepRunParam(Long projectId, Long pipelineId, Map<String, String> inParams, String outParamPrefix) {
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.inParams = inParams;
        this.outParamPrefix = outParamPrefix;
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

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }

    public String getOutParamPrefix() {
        return outParamPrefix;
    }

    public void setOutParamPrefix(String outParamPrefix) {
        this.outParamPrefix = outParamPrefix;
    }
}
