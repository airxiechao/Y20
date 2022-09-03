package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DownloadUserFileParam {
    @Required private Long userId;
    @Required private String path;

    public DownloadUserFileParam() {
    }

    public DownloadUserFileParam(String path) {
        this.path = path;
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
}
