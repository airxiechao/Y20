package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceAuthRest {

    @Get("/service/access/token/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<AccessTokenRecord> getAccessToken(Object exc);

    @Post("/service/access/token/validate")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response validateAccessToken(Object exc);

    @Post("/service/access/principal/extract")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<AccessPrincipal> extractAccessPrincipal(Object exc);
}
