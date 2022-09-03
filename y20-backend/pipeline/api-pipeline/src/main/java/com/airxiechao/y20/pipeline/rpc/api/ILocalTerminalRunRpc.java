package com.airxiechao.y20.pipeline.rpc.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;

@IRpc
public interface ILocalTerminalRunRpc {

    @Query("create-terminal-run")
    Response createTerminalRun(Object exc) throws Exception;

    @Query("destroy-terminal-run")
    Response destroyTerminalRun(Object exc);

    @Query("push-terminal-run-input")
    Response pushTerminalRunInput(Object exc) throws Exception;

}
