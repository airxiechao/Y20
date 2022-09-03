package com.airxiechao.y20.pipeline.rpc.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRpc
public interface IAgentStepRunRpc {

    @Query("push-step-run-output")
    @Auth(scope = EnumAccessScope.AGENT)
    Response pushStepRunOutput(Object exc);

    @Query("step-run-callback")
    @Auth(scope = EnumAccessScope.AGENT)
    Response stepRunCallback(Object exc);
}
