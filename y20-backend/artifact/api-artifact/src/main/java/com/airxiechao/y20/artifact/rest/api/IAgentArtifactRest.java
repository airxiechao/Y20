package com.airxiechao.y20.artifact.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.communication.rest.constant.EnumContentType;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

import java.util.List;

@IRest
public interface IAgentArtifactRest {

    // user file

    @Get("/agent/artifact/user/file/list")
    @Auth(scope = EnumAccessScope.AGENT)
    Response<List<FsFile>> listUserFile(Object exc);

    @Post(value = "/agent/artifact/user/file/upload", contentType = EnumContentType.FORM_MULTIPART)
    @Auth(scope = EnumAccessScope.AGENT)
    Response uploadUserFile(Object exc);

    @Get("/agent/artifact/user/file/download")
    @Auth(scope = EnumAccessScope.AGENT)
    void downloadUserFile(Object exc);

}
