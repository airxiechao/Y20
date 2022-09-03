package com.airxiechao.y20.pipeline.rpc.param.step;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class StopStepRpcParam {

    private String stepRunInstanceUuid;

    public StopStepRpcParam() {
    }

    public StopStepRpcParam(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }

    public String getStepRunInstanceUuid() {
        return stepRunInstanceUuid;
    }

    public void setStepRunInstanceUuid(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }
}
