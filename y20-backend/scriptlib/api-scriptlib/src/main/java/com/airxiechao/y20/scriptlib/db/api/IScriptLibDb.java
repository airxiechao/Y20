package com.airxiechao.y20.scriptlib.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.scriptlib.db.record.ScriptPieceRecord;

import java.util.List;

@IDb("mybatis-y20-scriptlib.xml")
public interface IScriptLibDb {
    ScriptPieceRecord get(Long userId, Long scriptPieceId);
    List<ScriptPieceRecord> list(
            Long userId,
            String name,
            String orderField,
            String orderType,
            Integer pageNo,
            Integer pageSize
    );
    long count(Long userId, String name);
    boolean insert(ScriptPieceRecord templateRecord);
    boolean update(ScriptPieceRecord templateRecord);
    boolean delete(Long userId, Long scriptPieceId);
}
