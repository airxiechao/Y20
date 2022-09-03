package com.airxiechao.y20.activity.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserActivityRest {

    @Get("/user/activity/list")
    @Auth(scope = EnumAccessScope.USER)
    Response list(Object exc);
}
