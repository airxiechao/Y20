package com.airxiechao.y20.pipeline.rpc.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;
import com.airxiechao.y20.common.pipeline.annotation.ValidateAgent;

@IRpc
public interface ILocalStepRpc {

    @Query("run-step")
    @ValidateAgent
    Response runStep(Object exc) throws Exception;

    @Query("stop-step")
    Response stopStep(Object exc) throws Exception;
}
