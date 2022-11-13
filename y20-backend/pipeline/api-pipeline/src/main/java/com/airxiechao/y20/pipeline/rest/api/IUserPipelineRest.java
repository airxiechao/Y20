package com.airxiechao.y20.pipeline.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserPipelineRest {

    @Get("/user/pipeline/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipeline(Object exc);

    @Post("/user/pipeline/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createPipeline(Object exc);

    @Post("/user/pipeline/create/hello")
    @Auth(scope = EnumAccessScope.USER)
    Response createPipelineHello(Object exc);

    @Post("/user/pipeline/publish")
    @Auth(scope = EnumAccessScope.USER)
    Response publishPipeline(Object exc);

    @Post("/user/pipeline/copy")
    @Auth(scope = EnumAccessScope.USER)
    Response copyPipeline(Object exc);

    @Get("/user/pipeline/basic/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelineBasic(Object exc);

    @Post("/user/pipeline/basic/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineBasic(Object exc);

    @Post("/user/pipeline/bookmark/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineBookmark(Object exc);

    @Post("/user/pipeline/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipeline(Object exc);

    // variable

    @Get("/user/pipeline/variable/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineVariable(Object exc);

    @Get("/user/pipeline/variable/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelineVariable(Object exc);

    @Post("/user/pipeline/variable/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createPipelineVariable(Object exc);

    @Post("/user/pipeline/variable/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineVariable(Object exc);

    @Post("/user/pipeline/variable/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipelineVariable(Object exc);

    // step

    @Get("/user/pipeline/step/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineStep(Object exc);

    @Get("/user/pipeline/step/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelineStep(Object exc);

    @Post("/user/pipeline/step/add")
    @Auth(scope = EnumAccessScope.USER)
    Response addPipelineStep(Object exc);

    @Post("/user/pipeline/step/copy")
    @Auth(scope = EnumAccessScope.USER)
    Response copyPipelineStep(Object exc);

    @Post("/user/pipeline/step/move")
    @Auth(scope = EnumAccessScope.USER)
    Response movePipelineStep(Object exc);

    @Post("/user/pipeline/step/disable")
    @Auth(scope = EnumAccessScope.USER)
    Response disablePipelineStep(Object exc);

    @Post("/user/pipeline/step/condition/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineStepCondition(Object exc);

    @Post("/user/pipeline/step/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineStep(Object exc);

    @Post("/user/pipeline/step/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipelineStep(Object exc);

    @Get("/user/pipeline/step/type/list")
    //@Auth(scope = EnumAccessScope.USER)
    Response listPipelineStepType(Object exc);

    @Get("/user/pipeline/step/type/get")
    //@Auth(scope = EnumAccessScope.USER)
    Response getPipelineStepType(Object exc);

    // run

    @Post("/user/pipeline/run/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createPipelineRun(Object exc);

    @Post("/user/pipeline/run/start")
    @Auth(scope = EnumAccessScope.USER)
    Response startPipelineRun(Object exc);

    @Post("/user/pipeline/run/forward")
    @Auth(scope = EnumAccessScope.USER)
    Response forwardPipelineRun(Object exc);

    @Post("/user/pipeline/run/stop")
    @Auth(scope = EnumAccessScope.USER)
    Response stopPipelineRun(Object exc);

    @Post("/user/pipeline/run/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipelineRun(Object exc);

    @Get("/user/pipeline/run/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineRun(Object exc);

    @Get("/user/pipeline/run/running/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineRunRunning(Object exc);

    @Get("/user/pipeline/run/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelineRun(Object exc);

    // terminal

    @Post("/user/pipeline/terminal/run/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createTerminalRun(Object exc);

    @Post("/user/pipeline/terminal/run/destroy")
    @Auth(scope = EnumAccessScope.USER)
    Response destroyTerminalRun(Object exc);

    @Post("/user/pipeline/terminal/run/input")
    @Auth(scope = EnumAccessScope.USER)
    Response inputTerminalRun(Object exc);

    // explorer

    @Post("/user/pipeline/explorer/run/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createExplorerRun(Object exc);

    @Post("/user/pipeline/explorer/run/destroy")
    @Auth(scope = EnumAccessScope.USER)
    Response destroyExplorerRun(Object exc);

    @Get("/user/pipeline/explorer/run/file/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listFileExplorerRun(Object exc);

    @Get("/user/pipeline/explorer/run/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadFileExplorerRun(Object exc);

    @Post("/user/pipeline/explorer/run/file/download/stop")
    @Auth(scope = EnumAccessScope.USER)
    Response stopDownloadFileExplorerRun(Object exc);

    @Post("/user/pipeline/explorer/run/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadFileExplorerRun(Object exc);

    @Post("/user/pipeline/explorer/run/file/upload/stop")
    @Auth(scope = EnumAccessScope.USER)
    Response stopUploadFileExplorerRun(Object exc);

    // webhook trigger

    @Post("/user/pipeline/trigger/webhook/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createPipelineWebhookTrigger(Object exc);

    @Post("/user/pipeline/trigger/webhook/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updatePipelineWebhookTrigger(Object exc);

    @Post("/user/pipeline/trigger/webhook/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipelineWebhookTrigger(Object exc);

    @Get("/user/pipeline/trigger/webhook/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelineWebhookTrigger(Object exc);

    @Get("/user/pipeline/trigger/webhook/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineWebhookTrigger(Object exc);

    // pending

    @Get("/user/pipeline/pending/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelinePending(Object exc);

    @Get("/user/pipeline/pending/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getPipelinePending(Object exc);

    @Post("/user/pipeline/pending/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deletePipelinePending(Object exc);
}
