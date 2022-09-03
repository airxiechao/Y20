package com.airxiechao.y20.quota.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserQuotaRest {

    @Get("/user/quota/free")
    //@Auth(scope = EnumAccessScope.USER)
    Response getFreeQuota(Object exc);

    @Get("/user/quota/usage")
    @Auth(scope = EnumAccessScope.USER)
    Response getQuotaUsage(Object exc);

    @Get("/user/quota/current")
    @Auth(scope = EnumAccessScope.USER)
    Response getCurrentQuota(Object exc);

    @Get("/user/quota/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listQuota(Object exc);
}
