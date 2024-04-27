package com.airxiechao.y20.monitor.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserMonitorRest {

    @Get("/user/monitor/list")
    @Auth(scope = EnumAccessScope.USER)
    Response list(Object exc);

    @Get("/user/monitor/metric/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listMonitorMetric(Object exc);

    @Get("/user/monitor/agent/metric/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listAgentMetric(Object exc);

    @Get("/user/monitor/get")
    @Auth(scope = EnumAccessScope.USER)
    Response get(Object exc);

    @Post("/user/monitor/basic/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateBasic(Object exc);

    @Post("/user/monitor/create")
    @Auth(scope = EnumAccessScope.USER)
    Response create(Object exc);

    @Post("/user/monitor/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response delete(Object exc);
}
