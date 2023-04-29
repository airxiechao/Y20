package com.airxiechao.y20.scriptlib.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.scriptlib.db.api.IScriptLibDb;
import com.airxiechao.y20.scriptlib.db.record.ScriptPieceRecord;

import java.util.List;

public class ScriptLibDbProcedure extends AbstractDbProcedure implements IScriptLibDb {

    public ScriptLibDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public ScriptPieceRecord get(Long userId, Long scriptPieceId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ScriptPieceRecord.class))
                .where(DbUtil.column(ScriptPieceRecord.class, "id"), "=", scriptPieceId);

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "=", userId);
        }else{
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "is", null);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, ScriptPieceRecord.class);
    }

    @Override
    public List<ScriptPieceRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ScriptPieceRecord.class));

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "=", userId);
        }else{
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "is", null);
        }

        if(null != name) {
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(ScriptPieceRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, ScriptPieceRecord.class);
    }

    @Override
    public long count(Long userId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ScriptPieceRecord.class));

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "=", userId);
        }else{
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "userId"), "is", null);
        }

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(ScriptPieceRecord.class, "name"), "like", "%" + name + "%");
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, ScriptPieceRecord.class);
    }

    @Override
    public boolean insert(ScriptPieceRecord scriptPieceRecord) {
        return dbManager.insert(scriptPieceRecord) > 0;
    }

    @Override
    public boolean update(ScriptPieceRecord scriptPieceRecord) {
        return dbManager.update(scriptPieceRecord) > 0;
    }

    @Override
    public boolean delete(Long userId, Long scriptPieceId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(ScriptPieceRecord.class))
                .where(DbUtil.column(ScriptPieceRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(ScriptPieceRecord.class, "id"), "=", scriptPieceId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, ScriptPieceRecord.class) > 0;
        return deleted;
    }
}
