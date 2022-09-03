package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class ScriptInDockerProcessStepRunParam {

    @StepTypeParam(
            displayName = "脚本",
            type = EnumPipelineParamType.TEXT)
    @Required
    private String script;

    @StepTypeParam(
            displayName = "执行程序",
            type = EnumPipelineParamType.INPUT)
    private String executor;

    @StepTypeParam(
            displayName = "工作目录",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String workingDir;

    @StepTypeParam(
            displayName = "输出变量",
            type = EnumPipelineParamType.INPUT)
    private String outputs;

    public ScriptInDockerProcessStepRunParam(String script, String executor, String workingDir, String outputs) {
        this.script = script;
        this.executor = executor;
        this.workingDir = workingDir;
        this.outputs = outputs;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }
}
