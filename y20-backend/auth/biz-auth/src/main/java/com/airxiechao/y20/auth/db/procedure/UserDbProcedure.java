package com.airxiechao.y20.auth.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.y20.auth.db.api.IUserDb;
import com.airxiechao.y20.auth.db.record.UserRecord;

public class UserDbProcedure extends AbstractDbProcedure implements IUserDb {

    public UserDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public UserRecord getByUsername(String username) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(UserRecord.class))
                .where(DbUtil.column(UserRecord.class, "username"), "=", username)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, UserRecord.class);
    }

    @Override
    public UserRecord getByMobile(String mobile) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(UserRecord.class))
                .where(DbUtil.column(UserRecord.class, "mobile"), "=", mobile)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, UserRecord.class);
    }

    @Override
    public UserRecord getByEmail(String email) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(UserRecord.class))
                .where(DbUtil.column(UserRecord.class, "email"), "=", email)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, UserRecord.class);
    }

    @Override
    public UserRecord getById(Long userId) {
        return this.dbManager.getById(userId, UserRecord.class);
    }

    @Override
    public boolean insert(UserRecord userRecord) {
        return this.dbManager.insert(userRecord) > 0;
    }

    @Override
    public boolean update(UserRecord userRecord) {
        return this.dbManager.update(userRecord) > 0;
    }
}
