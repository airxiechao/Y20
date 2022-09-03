package com.airxiechao.y20.pipeline.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceTerminalRunInstanceRest {

    @Post("/service/pipeline/terminal/run/instance/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<String> createTerminalRunInstance(Object exc);

    @Post("/service/pipeline/terminal/run/instance/destroy")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response destroyTerminalRunInstance(Object exc);

    @Post("/service/pipeline/terminal/run/instance/input")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response inputTerminalRunInstance(Object exc);

}
