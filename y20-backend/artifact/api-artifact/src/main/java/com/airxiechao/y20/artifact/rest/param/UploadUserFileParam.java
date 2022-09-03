package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UploadUserFileParam {
    @Required private Long userId;
    @Required private String path;
    @Required private FileData file;

    public UploadUserFileParam() {
    }

    public UploadUserFileParam(String path, FileData file) {
        this.path = path;
        this.file = file;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(FileData file) {
        this.file = file;
    }
}
