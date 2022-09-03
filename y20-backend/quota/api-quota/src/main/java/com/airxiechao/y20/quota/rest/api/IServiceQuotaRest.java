package com.airxiechao.y20.quota.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceQuotaRest {

    @Post("/service/quota/validate")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response validateQuota(Object exc);

    @Post("/service/quota/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response createQuota(Object exc);
}
