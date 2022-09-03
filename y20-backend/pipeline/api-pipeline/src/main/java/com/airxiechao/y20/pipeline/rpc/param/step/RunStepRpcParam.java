package com.airxiechao.y20.pipeline.rpc.param.step;

import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;

public class RunStepRpcParam {

    private PipelineStep step;
    private String stepRunInstanceUuid;
    private Env env;

    public RunStepRpcParam() {
    }

    public RunStepRpcParam(PipelineStep step, String stepRunInstanceUuid, Env env) {
        this.step = step;
        this.stepRunInstanceUuid = stepRunInstanceUuid;
        this.env = env;
    }

    public PipelineStep getStep() {
        return step;
    }

    public void setStep(PipelineStep step) {
        this.step = step;
    }

    public String getStepRunInstanceUuid() {
        return stepRunInstanceUuid;
    }

    public void setStepRunInstanceUuid(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }

    public Env getEnv() {
        return env;
    }

    public void setEnv(Env env) {
        this.env = env;
    }
}
