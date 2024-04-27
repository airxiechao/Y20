package com.airxiechao.y20.agent.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceAgentRest {
    @Get("/service/agent/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<AgentVo> getAgent(Object exc);

    @Get("/service/agent/count")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> countAgent(Object exc);

    @Post("/service/agent/update")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response updateAgent(Object exc);
}
