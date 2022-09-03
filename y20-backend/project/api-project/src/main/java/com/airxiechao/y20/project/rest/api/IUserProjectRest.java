package com.airxiechao.y20.project.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserProjectRest {

    @Get("/user/project/list")
    @Auth(scope = EnumAccessScope.USER)
    Response list(Object exc);

    @Get("/user/project/detail/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listDetail(Object exc);

    @Get("/user/project/basic/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getBasic(Object exc);

    @Post("/user/project/basic/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateBasic(Object exc);

    @Post("/user/project/bookmark/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateBookmark(Object exc);

    @Post("/user/project/create")
    @Auth(scope = EnumAccessScope.USER)
    Response create(Object exc);

    @Post("/user/project/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response delete(Object exc);

    // variables

    @Get("/user/project/variable/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listVariable(Object exc);

    @Get("/user/project/variable/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getVariable(Object exc);

    @Post("/user/project/variable/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createVariable(Object exc);

    @Post("/user/project/variable/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateVariable(Object exc);

    @Post("/user/project/variable/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deleteVariable(Object exc);
}
