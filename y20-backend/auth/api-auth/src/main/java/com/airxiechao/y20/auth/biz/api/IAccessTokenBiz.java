package com.airxiechao.y20.auth.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.pojo.AccessPolicy;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.pojo.exception.InvalidAccessTokenException;

import java.util.Date;
import java.util.List;

@IBiz
public interface IAccessTokenBiz {

    String hashAccessToken(String accessToken);
    boolean updateAccessToken(AccessTokenRecord accessTokenRecord);

    String createUserAccessToken(Long userId, String createIp);
    String createServiceAccessToken();
    String createAgentAccessToken(Long userId, String name, Date beginTime, Date endTime, String createIp);
    String createWebhookAccessToken(Long userId, String name, Date beginTime, Date endTime, String createIp);

    long countUserAccessToken(Long userId);
    List<AccessTokenRecord> listUserAccessToken(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countNotUserAccessToken(Long userId, String name);
    List<AccessTokenRecord> listNotUserAccessToken(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);

    AccessTokenRecord getByAccessToken(String accessToken);
    AccessTokenRecord getByIdAndUserId(Long id, Long userId);

    boolean deleteAccessToken(Long id);

    AccessPrincipal extractAccessPrincipal(String accessToken) throws InvalidAccessTokenException;
    boolean validateAccessToken(String accessToken, String targetAccessScope,
                                String targetAccessItem, Integer targetAccessMode);
}
