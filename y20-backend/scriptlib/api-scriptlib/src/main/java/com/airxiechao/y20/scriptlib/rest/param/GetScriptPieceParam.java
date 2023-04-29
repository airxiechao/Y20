package com.airxiechao.y20.scriptlib.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetScriptPieceParam {
    @Required private Long userId;
    private Boolean isPublic;
    @Required private Long scriptPieceId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Long getScriptPieceId() {
        return scriptPieceId;
    }

    public void setScriptPieceId(Long scriptPieceId) {
        this.scriptPieceId = scriptPieceId;
    }
}
