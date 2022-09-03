package com.airxiechao.y20.project.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.project.pojo.Project;

@IRest
public interface IServiceProjectRest {

    @Get("/service/project/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Project> get(Object exc);

    @Get("/service/project/exist")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response exist(Object exc);
}
