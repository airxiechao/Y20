package com.airxiechao.y20.scriptlib.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.scriptlib.pojo.ScriptPiece;

@Table(value = "script_piece")
@Index(fields = {"userId"})
public class ScriptPieceRecord {
    private Long id;
    private Long userId;
    @Column(length = 500) private String name;
    @Column(type = "text") private String script;

    public ScriptPiece toPojo(){
        ScriptPiece piece = new ScriptPiece();
        piece.setScriptPieceId(id);
        piece.setUserId(userId);
        piece.setName(name);
        piece.setScript(script);

        return piece;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
