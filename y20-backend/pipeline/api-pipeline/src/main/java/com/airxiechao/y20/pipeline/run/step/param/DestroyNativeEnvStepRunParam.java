package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class DestroyNativeEnvStepRunParam {

    @StepTypeParam(
            displayName = "环境实例Id",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String envInstanceUuid;

    public DestroyNativeEnvStepRunParam(String envInstanceUuid) {
        this.envInstanceUuid = envInstanceUuid;
    }

    public String getEnvInstanceUuid() {
        return envInstanceUuid;
    }

    public void setEnvInstanceUuid(String envInstanceUuid) {
        this.envInstanceUuid = envInstanceUuid;
    }
}
