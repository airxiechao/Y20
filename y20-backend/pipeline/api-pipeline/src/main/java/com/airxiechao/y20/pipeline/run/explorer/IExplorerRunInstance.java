package com.airxiechao.y20.pipeline.run.explorer;

import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.function.TriConsumer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IExplorerRunInstance {
    void create() throws Exception;
    void destroy() throws Exception;

    FsFile getFile(String path) throws Exception;
    List<FsFile> list(String path) throws Exception;
    void download(String path, OutputStream outputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception;
    void stopDownload(String path);
    void upload(String path, long size, Long maxSpeed, InputStream inputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception;
    void stopUpload(String path);

    String createFileHolder(String path, long size) throws Exception;
    boolean pushFilePart(String holderId, long pos, byte[] bytes) throws Exception;
    String saveFileHolder(String holderId) throws Exception;
    boolean removeFileHolder(String holderId) throws Exception;
    byte[] pullFilePart(String path, long pos, int length) throws Exception;

    String getExplorerRunInstanceUuid();
}
