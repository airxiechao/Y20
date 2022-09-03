package com.airxiechao.y20.pipeline.rest.api;

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
public interface IServiceExplorerRunInstanceRest {

    @Post("/service/pipeline/explorer/run/instance/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<String> createExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/destroy")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response destroyExplorerRunInstance(Object exc);

    @Get("/service/pipeline/explorer/run/instance/file/list")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<List<FsFile>> listFileExplorerRunInstance(Object exc);

    @Get("/service/pipeline/explorer/run/instance/file/download")
    @Auth(scope = EnumAccessScope.SERVICE)
    void downloadFileExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/file/download/stop")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response stopDownloadFileExplorerRunInstance(Object exc);

    @Post(value = "/service/pipeline/explorer/run/instance/file/upload", contentType = EnumContentType.FORM_MULTIPART)
    @Auth(scope = EnumAccessScope.SERVICE)
    Response uploadFileExplorerRunInstance(Object exc);

    @Post(value = "/service/pipeline/explorer/run/instance/file/upload/stop")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response stopUploadFileExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/file/holder/create")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<String> createFileHolderExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/file/part/push")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response pushFilePartExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/file/holder/save")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<String> saveFileHolderExplorerRunInstance(Object exc);

    @Post("/service/pipeline/explorer/run/instance/file/holder/remove")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response removeFileHolderExplorerRunInstance(Object exc);

    @Get("/service/pipeline/explorer/run/instance/file/part/pull")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<byte[]> pullFilePartExplorerRunInstance(Object exc);

}
