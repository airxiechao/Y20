package com.airxiechao.y20.artifact.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserArtifactRest {

    // user file

    @Get("/user/artifact/user/file/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listUserFile(Object exc);

    @Post(value = "/user/artifact/user/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadUserFile(Object exc);

    @Post("/user/artifact/user/file/remove")
    @Auth(scope = EnumAccessScope.USER)
    Response removeUserFile(Object exc);

    @Get("/user/artifact/user/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadUserFile(Object exc);

    // project file

    @Get("/user/artifact/project/file/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listProjectFile(Object exc);

    @Post("/user/artifact/project/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadProjectFile(Object exc);

    @Post("/user/artifact/project/file/remove")
    @Auth(scope = EnumAccessScope.USER)
    Response removeProjectFile(Object exc);

    @Get("/user/artifact/project/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadProjectFile(Object exc);

    // pipeline file

    @Get("/user/artifact/pipeline/file/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineFile(Object exc);

    @Post("/user/artifact/pipeline/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadPipelineFile(Object exc);

    @Post("/user/artifact/pipeline/file/remove")
    @Auth(scope = EnumAccessScope.USER)
    Response removePipelineFile(Object exc);

    @Get("/user/artifact/pipeline/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadPipelineFile(Object exc);

    // pipeline step file

    @Post("/user/artifact/pipeline/step/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadPipelineStepFile(Object exc);

    @Get("/user/artifact/pipeline/step/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadPipelineStepFile(Object exc);

    // pipeline temp file

    @Post("/user/artifact/pipeline/temp/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadPipelineTempFile(Object exc);

    @Get("/user/artifact/pipeline/temp/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadPipelineTempFile(Object exc);

    // pipeline run file

    @Get("/user/artifact/pipeline/run/file/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listPipelineRunFile(Object exc);

    @Post("/user/artifact/pipeline/run/file/upload")
    @Auth(scope = EnumAccessScope.USER)
    Response uploadPipelineRunFile(Object exc);

    @Get("/user/artifact/pipeline/run/file/download")
    @Auth(scope = EnumAccessScope.USER)
    void downloadPipelineRunFile(Object exc);

    // user file

    @Get("/user/artifact/template/file/list")
    //@Auth(scope = EnumAccessScope.USER)
    Response listTemplateFile(Object exc);

    @Get("/user/artifact/template/file/download")
    //@Auth(scope = EnumAccessScope.USER)
    void downloadTemplateFile(Object exc);
}
