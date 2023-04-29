package com.airxiechao.y20.scriptlib.biz.process;

import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.scriptlib.biz.api.IScriptLibBiz;
import com.airxiechao.y20.scriptlib.db.api.IScriptLibDb;
import com.airxiechao.y20.scriptlib.pojo.ScriptPiece;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptLibBizProcess implements IScriptLibBiz {

    private IScriptLibDb scriptLibDb = Db.get(IScriptLibDb.class);

    @Override
    public List<ScriptPiece> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return scriptLibDb.list(userId, name, orderField, orderType, pageNo, pageSize)
                .stream().map(p -> p.toPojo()).collect(Collectors.toList());
    }

    @Override
    public ScriptPiece get(Long userId, Long scriptPieceId) {
        return scriptLibDb.get(userId, scriptPieceId).toPojo();
    }

    @Override
    public long count(Long userId, String name) {
        return scriptLibDb.count(userId, name);
    }

    @Override
    public boolean create(ScriptPiece piece) {
        return scriptLibDb.insert(piece.toRecord());
    }

    @Override
    public boolean update(ScriptPiece piece) {
        return scriptLibDb.update(piece.toRecord());
    }

    @Override
    public boolean delete(Long userId, Long scriptPieceId) {
        return scriptLibDb.delete(userId, scriptPieceId);
    }
}
