package com.airxiechao.y20.auth.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.auth.db.record.UserRecord;

@IDb("mybatis-y20-auth.xml")
public interface IUserDb {

    UserRecord getByUsername(String username);
    UserRecord getByMobile(String mobile);
    UserRecord getByEmail(String email);
    UserRecord getById(Long userId);
    boolean insert(UserRecord userRecord);
    boolean update(UserRecord userRecord);
}
