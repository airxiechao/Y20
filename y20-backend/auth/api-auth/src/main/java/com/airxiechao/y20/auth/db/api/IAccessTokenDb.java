package com.airxiechao.y20.auth.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;

import java.util.List;

@IDb("mybatis-y20-auth.xml")
public interface IAccessTokenDb {

    AccessTokenRecord getByTokenHashed(String token);
    AccessTokenRecord getByIdAndUserId(Long id, Long userId);
    boolean insert(AccessTokenRecord accessTokenRecord);
    boolean update(AccessTokenRecord accessTokenRecord);
    boolean delete(Long id);

    long countUserAccessToken(Long userId);
    List<AccessTokenRecord> listUserAccessToken(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countNotUserAccessToken(Long userId, String name);
    List<AccessTokenRecord> listNotUserAccessToken(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);

}
