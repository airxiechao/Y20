package com.airxiechao.y20.pipeline.rpc.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRpc
public interface IAgentTerminalRunRpc {
    @Query("push-terminal-run-output")
    @Auth(scope = EnumAccessScope.AGENT)
    Response pushTerminalRunOutput(Object exc);
}
