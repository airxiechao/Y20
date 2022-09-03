package com.airxiechao.y20.pipeline.rpc.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Query;
import com.airxiechao.axcboot.core.annotation.IRpc;
import com.airxiechao.axcboot.storage.fs.common.FsFile;

import java.util.List;

@IRpc
public interface ILocalExplorerRunRpc {

    @Query("create-explorer-run")
    Response<String> createExplorerRun(Object exc) throws Exception;

    @Query("destroy-explorer-run")
    Response destroyExplorerRun(Object exc) throws Exception;

    @Query("get-explorer-run-file")
    Response<FsFile> getExplorerRunFile(Object exc) throws Exception;

    @Query("list-explorer-run")
    Response<List<FsFile>> listExplorerRun(Object exc) throws Exception;

    @Query("pull-explorer-run-file-part")
    Response<byte[]> pullExplorerRunFilePart(Object exc) throws Exception;

    @Query("push-explorer-run-file-part")
    Response pushExplorerRunFilePart(Object exc) throws Exception;

    @Query("create-explorer-run-file-holder")
    Response<String> createExplorerRunFileHolder(Object exc) throws Exception;

    @Query("save-explorer-run-file-holder")
    Response<String> saveExplorerRunFileHolder(Object exc) throws Exception;

    @Query("remove-explorer-run-file-holder")
    Response removeExplorerRunFileHolder(Object exc) throws Exception;

}
