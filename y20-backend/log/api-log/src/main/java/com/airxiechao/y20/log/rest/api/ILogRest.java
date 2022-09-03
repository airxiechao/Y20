package com.airxiechao.y20.log.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface ILogRest {

    @Get("/user/log/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getLog(Object exc);

    @Get("/user/log/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadLog(Object exc);
}
