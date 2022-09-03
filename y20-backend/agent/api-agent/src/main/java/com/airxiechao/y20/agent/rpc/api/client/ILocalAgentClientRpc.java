package com.airxiechao.y20.agent.rpc.api.client;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;

@IRpc
public interface ILocalAgentClientRpc {

    @Query("upgrade-agent-client")
    Response upgrade(Object exc) throws Exception;

    @Query("uninstall-agent-client")
    Response uninstall(Object exc) throws Exception;

    @Query("restart-agent-client")
    Response restart(Object exc) throws Exception;

    @Query("read-agent-client-config")
    Response<String> readConfig(Object exc) throws Exception;

    @Query("save-agent-client-config")
    Response saveConfig(Object exc) throws Exception;

    @Query("clean-agent-client")
    Response clean(Object exc) throws Exception;
}
