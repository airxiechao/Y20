package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class TestStepRunParam {

    @StepTypeParam(
            displayName = "i",
            type = EnumPipelineParamType.INPUT)
    @Required
    private Integer i;

    @StepTypeParam(
            displayName = "脚本",
            type = EnumPipelineParamType.TEXT)
    @Required
    private String script;

    public TestStepRunParam(Integer i, String script) {
        this.i = i;
        this.script = script;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
