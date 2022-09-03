package com.airxiechao.y20.template.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceTemplateRest {

    @Post("/service/template/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> createTemplate(Object exc);

    @Post("/service/template/update")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> updateTemplate(Object exc);

}
