package com.airxiechao.y20.scriptlib.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.scriptlib.pojo.ScriptPiece;

import java.util.List;

@IBiz
public interface IScriptLibBiz {
    List<ScriptPiece> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);
    ScriptPiece get(Long userId, Long scriptPieceId);
    long count(Long userId, String name);
    boolean create(ScriptPiece piece);
    boolean update(ScriptPiece piece);
    boolean delete(Long userId, Long scriptPieceId);
}
