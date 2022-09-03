package com.airxiechao.y20.pipeline.rpc.param.step;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class StepRunCallbackRpcParam {

    private String pipelineRunInstanceUuid;
    private String stepRunInstanceUuid;
    private Response response;

    public StepRunCallbackRpcParam() {
    }

    public StepRunCallbackRpcParam(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Response response) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.stepRunInstanceUuid = stepRunInstanceUuid;
        this.response = response;
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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
