package com.airxiechao.y20.scriptlib.pojo;

import com.airxiechao.y20.scriptlib.db.record.ScriptPieceRecord;

public class ScriptPiece {
    private Long scriptPieceId;
    private Long userId;
    private String name;
    private String script;

    public ScriptPieceRecord toRecord(){
        ScriptPieceRecord record = new ScriptPieceRecord();
        record.setId(scriptPieceId);
        record.setUserId(userId);
        record.setName(name);
        record.setScript(script);

        return record;
    }

    public Long getScriptPieceId() {
        return scriptPieceId;
    }

    public void setScriptPieceId(Long scriptPieceId) {
        this.scriptPieceId = scriptPieceId;
    }

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
