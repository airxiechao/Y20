package com.airxiechao.y20.artifact.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceArtifactRest {

    // pipeline file

    @Post("/user/artifact/pipeline/file/copy")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response copyPipelineFile(Object exc);

    @Post("/user/artifact/template/file/copy/from")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response copyTemplateFileFrom(Object exc);

    // template file

    @Post("/user/artifact/template/file/copy/to")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response copyTemplateFileTo(Object exc);

    @Post("/user/artifact/template/file/remove")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response removeTemplateFile(Object exc);
}
