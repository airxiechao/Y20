package com.airxiechao.y20.scriptlib.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeleteScriptPieceParam {
    @Required private Long userId;
    @Required private Long scriptPieceId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScriptPieceId() {
        return scriptPieceId;
    }

    public void setScriptPieceId(Long scriptPieceId) {
        this.scriptPieceId = scriptPieceId;
    }
}
