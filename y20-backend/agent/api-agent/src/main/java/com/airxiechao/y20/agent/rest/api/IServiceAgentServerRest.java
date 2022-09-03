package com.airxiechao.y20.agent.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceAgentServerRest {

    @Post("/service/agent/call")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response callAgent(Object exc);

    @Get("/service/agent/active")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response isAgentActive(Object exc);
}
