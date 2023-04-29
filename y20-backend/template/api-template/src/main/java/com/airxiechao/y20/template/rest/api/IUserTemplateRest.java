package com.airxiechao.y20.template.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserTemplateRest {

    @Get("/user/template/basic/get")
    //@Auth(scope = EnumAccessScope.USER)
    Response getBasic(Object exc);

    @Get("/user/template/full/get")
    //@Auth(scope = EnumAccessScope.USER)
    Response getFull(Object exc);

    @Get("/user/template/list")
    //@Auth(scope = EnumAccessScope.USER)
    Response list(Object exc);

    @Get("/user/template/list/my")
    @Auth(scope = EnumAccessScope.USER)
    Response listMy(Object exc);

    @Get("/user/template/recommend")
    //@Auth(scope = EnumAccessScope.USER)
    Response recommend(Object exc);

    @Post("/user/template/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response delete(Object exc);

    @Post("/user/template/apply")
    @Auth(scope = EnumAccessScope.USER)
    Response apply(Object exc);
}
