package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeleteAccessTokenParam {
    @Required private Long userId;
    @Required private Long accessTokenId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(Long accessTokenId) {
        this.accessTokenId = accessTokenId;
    }
}
