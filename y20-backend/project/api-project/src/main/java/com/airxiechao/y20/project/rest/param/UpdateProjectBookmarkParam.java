package com.airxiechao.y20.project.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateProjectBookmarkParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Boolean bookmarked;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}
