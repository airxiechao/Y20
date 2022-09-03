package com.airxiechao.y20.auth.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.db.api.IAccessTokenDb;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

import java.util.Date;
import java.util.List;

public class AccessTokenDbProcedure extends AbstractDbProcedure implements IAccessTokenDb {

    public AccessTokenDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public AccessTokenRecord getByTokenHashed(String tokenHashed) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "tokenHashed"), "=", tokenHashed)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, AccessTokenRecord.class);
    }

    @Override
    public AccessTokenRecord getByIdAndUserId(Long id, Long userId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "id"), "=", id)
                .where(DbUtil.column(AccessTokenRecord.class, "userId"), "=", userId)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, AccessTokenRecord.class);
    }

    @Override
    public boolean insert(AccessTokenRecord accessTokenRecord) {
        return this.dbManager.insert(accessTokenRecord) > 0;
    }

    @Override
    public boolean update(AccessTokenRecord accessTokenRecord) {
        return this.dbManager.update(accessTokenRecord) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return this.dbManager.deleteById(id, AccessTokenRecord.class) > 0;
    }

    @Override
    public long countUserAccessToken(Long userId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AccessTokenRecord.class, "scope"), "=", EnumAccessScope.USER)
                .where(DbUtil.column(AccessTokenRecord.class, "endTime"), ">", new Date())
                .count()
                .build();

        return dbManager.longBySql(sqlParams, AccessTokenRecord.class);
    }

    @Override
    public List<AccessTokenRecord> listUserAccessToken(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AccessTokenRecord.class, "scope"), "=", EnumAccessScope.USER)
                .where(DbUtil.column(AccessTokenRecord.class, "endTime"), ">", new Date());


        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(AccessTokenRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, AccessTokenRecord.class);
    }

    @Override
    public long countNotUserAccessToken(Long userId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AccessTokenRecord.class, "scope"), "!=", EnumAccessScope.USER);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(AccessTokenRecord.class, "name"), "like", "%" + name + "%");
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return dbManager.longBySql(sqlParams, AccessTokenRecord.class);
    }

    @Override
    public List<AccessTokenRecord> listNotUserAccessToken(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AccessTokenRecord.class))
                .where(DbUtil.column(AccessTokenRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AccessTokenRecord.class, "scope"), "!=", EnumAccessScope.USER);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(AccessTokenRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(AccessTokenRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, AccessTokenRecord.class);
    }
}
