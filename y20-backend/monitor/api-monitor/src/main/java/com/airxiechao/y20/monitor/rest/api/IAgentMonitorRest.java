package com.airxiechao.y20.monitor.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.monitor.pojo.Monitor;

import java.util.List;

@IRest
public interface IAgentMonitorRest {

    @Get("/agent/monitor/list")
    @Auth(scope = EnumAccessScope.AGENT)
    Response<List<Monitor>> list(Object exc);

    @Post("/agent/monitor/status/update")
    @Auth(scope = EnumAccessScope.AGENT)
    Response updateStatus(Object exc);

    @Post("/agent/monitor/agent/status/update")
    @Auth(scope = EnumAccessScope.AGENT)
    Response updateAgentStatus(Object exc);
}
