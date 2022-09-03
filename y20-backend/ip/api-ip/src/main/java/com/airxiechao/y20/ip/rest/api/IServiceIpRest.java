package com.airxiechao.y20.ip.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.ip.pojo.IpLocation;

@IRest
public interface IServiceIpRest {

    @Get("/service/ip/resolve")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<IpLocation> resolve(Object exc);

}
