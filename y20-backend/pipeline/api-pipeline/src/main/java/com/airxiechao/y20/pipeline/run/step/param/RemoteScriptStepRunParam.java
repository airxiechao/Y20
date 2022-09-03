package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class RemoteScriptStepRunParam {

    @StepTypeParam(
            displayName = "脚本",
            type = EnumPipelineParamType.TEXT)
    @Required
    private String script;

    @StepTypeParam(
            displayName = "执行程序",
            hint = "脚本的执行程序。Windows系统默认为cmd，支持powershell，Linux系统默认为bash",
            type = EnumPipelineParamType.INPUT)
    private String executor;

    @StepTypeParam(
            displayName = "输出变量",
            hint = "脚本中要导出为步骤输出的变量名，多个以英文逗号（,）分隔",
            type = EnumPipelineParamType.INPUT)
    private String outputs;

    public RemoteScriptStepRunParam(String script, String executor, String outputs) {
        this.script = script;
        this.executor = executor;
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

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }
}
