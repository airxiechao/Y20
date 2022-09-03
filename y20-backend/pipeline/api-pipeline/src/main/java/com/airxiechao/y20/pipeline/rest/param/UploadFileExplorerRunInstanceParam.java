package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UploadFileExplorerRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String explorerRunInstanceUuid;
    @Required private String path;
    @Required private Long size;
    @Required private FileData file;

    public UploadFileExplorerRunInstanceParam() {
    }

    public UploadFileExplorerRunInstanceParam(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, Long size, FileData file) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
        this.size = size;
        this.file = file;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(FileData file) {
        this.file = file;
    }
}
