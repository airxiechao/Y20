package com.airxiechao.y20.pipeline.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicPageVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineCountVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;

import java.util.Set;


@IRest
public interface IServicePipelineRest {

    @Get("/service/pipeline/basic/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<PipelineBasicVo> getPipelineBasic(Object exc);

    @Get("/service/pipeline/count")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<PipelineCountVo> countPipeline(Object exc);

    @Get("/service/pipeline/scheduled/list")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<PipelineBasicPageVo> listScheduledPipeline(Object exc);

    @Post("/service/pipeline/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> createPipeline(Object exc);

    @Post("/service/pipeline/delete")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> deletePipeline(Object exc);

    @Post("/service/pipeline/project/all/delete")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> deleteProjectAllPipeline(Object exc);

    // run

    @Get("/service/pipeline/run/running/agent/list")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Set<String>> listPipelineRunRunningAgent(Object exc);

    @Get("/service/pipeline/run/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<PipelineRunDetailVo> getPipelineRun(Object exc);

    @Get("/service/pipeline/run/count")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> countPipelineRun(Object exc);

    @Post("/service/pipeline/run/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long> createPipelineRun(Object exc);

    @Post("/service/pipeline/run/start")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<Long>  startPipelineRun(Object exc);
}
