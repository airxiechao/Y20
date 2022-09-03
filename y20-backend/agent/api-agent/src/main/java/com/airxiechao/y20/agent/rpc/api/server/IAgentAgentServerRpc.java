package com.airxiechao.y20.agent.rpc.api.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

import java.util.List;

@IRpc
public interface IAgentAgentServerRpc {

    @Query("register-agent")
    @Auth(scope = EnumAccessScope.AGENT)
    Response registerAgent(Object exc);

    @Query("get-rest-service-path")
    @Auth(scope = EnumAccessScope.AGENT)
    Response<String> getRestServicePath(Object exc);

}
