package com.airxiechao.y20.pipeline.rpc.param.step;

public class PushStepRunLogRpcParam {

    private String pipelineRunInstanceUuid;
    private String stepRunInstanceUuid;
    private String output;

    public PushStepRunLogRpcParam() {
    }

    public PushStepRunLogRpcParam(String pipelineRunInstanceUuid, String stepRunInstanceUuid, String output) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.stepRunInstanceUuid = stepRunInstanceUuid;
        this.output = output;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getStepRunInstanceUuid() {
        return stepRunInstanceUuid;
    }

    public void setStepRunInstanceUuid(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
