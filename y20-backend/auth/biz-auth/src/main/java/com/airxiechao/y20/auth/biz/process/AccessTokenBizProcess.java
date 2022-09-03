package com.airxiechao.y20.auth.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.db.api.IAccessTokenDb;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.pojo.AccessPolicy;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.pojo.config.AuthConfig;
import com.airxiechao.y20.auth.pojo.exception.InvalidAccessTokenException;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.airxiechao.y20.common.core.db.Db;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AccessTokenBizProcess implements IAccessTokenBiz {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenBizProcess.class);
    private static AuthConfig authConfig = ConfigFactory.get(AuthConfig.class);

    private IAccessTokenDb accessTokenDb = Db.get(IAccessTokenDb.class);

    @Override
    public String hashAccessToken(String accessToken) {
        return AuthUtil.hash(accessToken);
    }

    @Override
    public boolean updateAccessToken(AccessTokenRecord accessTokenRecord) {
        return accessTokenDb.update(accessTokenRecord);
    }

    @Override
    public String createUserAccessToken(Long userId, String createIp) {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.setPermitAll(true);

        Date beginTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginTime);
        cal.add(Calendar.MINUTE, authConfig.getNumMinuteOfUserAccessToken());
        Date endTime =  cal.getTime();

        return createAccessToken(userId, null, EnumAccessScope.USER, accessPolicy, beginTime, endTime, createIp);
    }

    @Override
    public String createServiceAccessToken() {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.setPermitAll(true);

        return createAccessToken(null, null, EnumAccessScope.SERVICE, accessPolicy, null, null, null);
    }

    @Override
    public String createAgentAccessToken(Long userId, String name, Date beginTime, Date endTime, String createIp) {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.setPermitAll(true);

        return createAccessToken(userId, name, EnumAccessScope.AGENT, accessPolicy, beginTime, endTime, createIp);
    }

    @Override
    public String createWebhookAccessToken(Long userId, String name, Date beginTime, Date endTime, String createIp) {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.setPermitAll(true);

        return createAccessToken(userId, name, EnumAccessScope.WEBHOOK, accessPolicy, beginTime, endTime, createIp);

    }

    @Override
    public long countUserAccessToken(Long userId) {
        return accessTokenDb.countUserAccessToken(userId);
    }

    @Override
    public List<AccessTokenRecord> listUserAccessToken(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return accessTokenDb.listUserAccessToken(userId, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long countNotUserAccessToken(Long userId, String name) {
        return accessTokenDb.countNotUserAccessToken(userId, name);
    }

    @Override
    public List<AccessTokenRecord> listNotUserAccessToken(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return accessTokenDb.listNotUserAccessToken(userId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public AccessTokenRecord getByAccessToken(String accessToken) {
        String tokenHashed = AuthUtil.hash(accessToken);
        AccessTokenRecord accessTokenRecord = accessTokenDb.getByTokenHashed(tokenHashed);
        return accessTokenRecord;
    }

    @Override
    public AccessTokenRecord getByIdAndUserId(Long id, Long userId) {
        return accessTokenDb.getByIdAndUserId(id, userId);
    }

    @Override
    public boolean deleteAccessToken(Long id) {
        return accessTokenDb.delete(id);
    }

    @Override
    public AccessPrincipal extractAccessPrincipal(String accessToken) throws InvalidAccessTokenException{
        String token = null;
        try {
            token = AuthUtil.decrypt(accessToken);
        } catch (Exception e) {
            throw new InvalidAccessTokenException();
        }
        AccessPrincipal accessPrincipal = JSON.parseObject(token, AccessPrincipal.class);

        return accessPrincipal;
    }

    @Override
    public boolean validateAccessToken(String accessToken, String targetAccessScope,
                                       String targetAccessItem, Integer targetAccessMode) {
        AccessPrincipal accessPrincipal = null;
        try {
            accessPrincipal = extractAccessPrincipal(accessToken);
        } catch (InvalidAccessTokenException e) {
            return false;
        }

        // check scope
        if(!targetAccessScope.equals(accessPrincipal.getAccessScope())){
            return false;
        }

        // check time
        Date now = new Date();
        if(null != accessPrincipal.getBeginTime() && now.before(accessPrincipal.getBeginTime())){
            return false;
        }
        if(null != accessPrincipal.getEndTime() && now.after(accessPrincipal.getEndTime())){
            return false;
        }

        // check exist for access token
        if(!EnumAccessScope.SERVICE.equals(accessPrincipal.getAccessScope())){
            String tokenHashed = AuthUtil.hash(accessToken);

            AccessTokenRecord accessTokenRecord = accessTokenDb.getByTokenHashed(tokenHashed);
            if(null == accessTokenRecord){
                return false;
            }
        }

        // check policy
        AccessPolicy accessPolicy = accessPrincipal.getAccessPolicy();
        if(null == accessPolicy){
            return false;
        }

        if(accessPolicy.isPermitAll()){
            return true;
        }

        if(!StringUtil.isBlank(targetAccessItem)) {
            if (!accessPolicy.permitItemMode(targetAccessItem, targetAccessMode)) {
                return false;
            }
        }

        return true;
    }

    private String createAccessToken(Long userId, String name, String accessScope, AccessPolicy accessPolicy, Date beginTime, Date endTime, String createIp) {
        AccessPrincipal accessPrincipal = new AccessPrincipal();
        accessPrincipal.setUuid(UuidUtil.random());
        accessPrincipal.setUserId(userId);
        accessPrincipal.setAccessScope(accessScope);
        accessPrincipal.setAccessPolicy(accessPolicy);
        accessPrincipal.setBeginTime(beginTime);
        accessPrincipal.setEndTime(endTime);

        String token = AuthUtil.encrypt(JSON.toJSONString(accessPrincipal));
        String tokenHashed = AuthUtil.hash(token);

        // save access token to db
        if(!EnumAccessScope.SERVICE.equals(accessScope)) {
            AccessTokenRecord accessTokenRecord = new AccessTokenRecord();
            accessTokenRecord.setUserId(userId);
            accessTokenRecord.setName(name);
            accessTokenRecord.setTokenHashed(tokenHashed);
            accessTokenRecord.setScope(accessPrincipal.getAccessScope());
            accessTokenRecord.setPolicy(accessPrincipal.getAccessPolicy());
            accessTokenRecord.setBeginTime(beginTime);
            accessTokenRecord.setEndTime(endTime);
            accessTokenRecord.setCreateTime(new Date());
            accessTokenRecord.setCreateIp(createIp);

            boolean inserted = accessTokenDb.insert(accessTokenRecord);
            if (!inserted) {
                return null;
            }
        }

        return token;
    }
}
