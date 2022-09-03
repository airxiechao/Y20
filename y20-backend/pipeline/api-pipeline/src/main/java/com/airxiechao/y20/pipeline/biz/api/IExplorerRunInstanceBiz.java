package com.airxiechao.y20.pipeline.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.function.TriConsumer;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@IBiz
public interface IExplorerRunInstanceBiz {
    IExplorerRunInstance createExplorerRunInstance(String pipelineRunInstanceUuid) throws Exception;
    void destroyExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid) throws Exception;

    List<FsFile> listFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception;
    void downloadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, OutputStream outputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception;
    void stopDownloadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception;
    void uploadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long size, Long maxSpeed, InputStream inputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception;
    void stopUploadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception;

    String createFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long size) throws Exception;
    boolean pushFilePartExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId, long pos, byte[] bytes) throws Exception;
    String saveFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId) throws Exception;
    boolean removeFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId) throws Exception;
    byte[] pullFilePartExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long pos, int length) throws Exception;
}
