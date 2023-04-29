package com.airxiechao.y20.scriptlib.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CreateScriptPieceParam {
    @Required private Long userId;
    @Required private String name;
    @Required private String script;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
