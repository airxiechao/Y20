package com.airxiechao.y20.pipeline.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;

@IRest
public interface IServicePipelineRunInstanceRest {

    @Get("/service/pipeline/run/instance/list")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response listPipelineRunInstance(Object exc);

    @Post("/service/pipeline/run/instance/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response createPipelineRunInstance(Object exc);

    @Post("/service/pipeline/run/instance/start")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response startPipelineRunInstance(Object exc);

    @Post("/service/pipeline/run/instance/forward")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response forwardPipelineRunInstance(Object exc);

    @Post("/service/pipeline/run/instance/stop")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response stopPipelineRunInstance(Object exc);

    @Post("/service/pipeline/step/run/instance/callback")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response callbackPipelineStepRunInstance(Object exc);

}
