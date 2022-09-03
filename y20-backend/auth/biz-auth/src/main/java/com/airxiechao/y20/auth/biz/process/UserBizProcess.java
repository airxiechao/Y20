package com.airxiechao.y20.auth.biz.process;

import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.biz.api.ITwoFactorBiz;
import com.airxiechao.y20.auth.biz.api.IUserBiz;
import com.airxiechao.y20.auth.db.api.IUserDb;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.pojo.vo.LoginVo;
import com.airxiechao.y20.auth.pojo.vo.TwoFactorSecretVo;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.db.Db;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UserBizProcess implements IUserBiz {

    private static final Logger logger = LoggerFactory.getLogger(UserBizProcess.class);

    private IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);
    private ITwoFactorBiz twoFactorBiz = Biz.get(ITwoFactorBiz.class);
    private IUserDb userDb = Db.get(IUserDb.class);

    @Override
    public UserRecord getByMobile(String mobile) {
        return userDb.getByMobile(mobile);
    }

    @Override
    public UserRecord getByEmail(String email) {
        return userDb.getByEmail(email);
    }

    @Override
    public UserRecord getByUsername(String username) {
        return userDb.getByUsername(username);
    }

    @Override
    public UserRecord createUser(String username, String password, String mobile) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUsername(username);
        userRecord.setMobile(mobile);
        userRecord.setCreateTime(new Date());

        String passwordHashed = AuthUtil.hashPassword(password);

        userRecord.setPasswordHashed(passwordHashed);

        boolean inserted = userDb.insert(userRecord);
        if(!inserted){
            return null;
        }

        return userRecord;
    }

    @Override
    public boolean validateUser(String username, String password) {
        UserRecord userRecord = userDb.getByUsername(username);
        if(null == userRecord){
            return false;
        }

        String passwordHashed = AuthUtil.hashPassword(password);

        return passwordHashed.equals(userRecord.getPasswordHashed());
    }

    @Override
    public LoginVo loginUser(String username, String password, String ip) throws Exception{

        boolean validated = validateUser(username, password);
        if(!validated){
            throw new Exception("validate user fail");
        }

        UserRecord userRecord = userDb.getByUsername(username);

        String accessToken = accessTokenBiz.createUserAccessToken(userRecord.getId(), ip);

        return new LoginVo(username, accessToken);
    }

    @Override
    public LoginVo loginUser(String username, String ip) throws Exception {
        UserRecord userRecord = userDb.getByUsername(username);
        if(null == userRecord){
            throw new Exception("no user");
        }

        String accessToken = accessTokenBiz.createUserAccessToken(userRecord.getId(), ip);

        return new LoginVo(username, accessToken);
    }

    @Override
    public UserRecord getUser(Long userId) {
        return userDb.getById(userId);
    }

    @Override
    public boolean updateUserMobile(Long userId, String mobile) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        userRecord.setMobile(mobile);
        return userDb.update(userRecord);
    }

    @Override
    public boolean updateUserEmail(Long userId, String email) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        userRecord.setEmail(email);
        return userDb.update(userRecord);
    }

    @Override
    public boolean updateUserPassword(Long userId, String password) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        String passwordHashed = AuthUtil.hashPassword(password);
        userRecord.setPasswordHashed(passwordHashed);
        return userDb.update(userRecord);
    }

    @Override
    public TwoFactorSecretVo createUserTwoFactorSecret(Long userId) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        String secret = twoFactorBiz.generateTwoFactorSecret();

        userRecord.setTwoFactorSecret(secret);

        boolean updated  = userDb.update(userRecord);
        if(!updated){
            throw new Exception("create user two factor secret error");
        }

        return new TwoFactorSecretVo(userRecord.getUsername(), secret);
    }

    @Override
    public boolean enableUserTwoFactor(Long userId, String code) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        String secret = userRecord.getTwoFactorSecret();
        if(StringUtil.isBlank(secret)){
            throw new Exception("no secret");
        }

        boolean passed = twoFactorBiz.verifyTwoFactorCode(secret, code);
        if(!passed){
            throw new Exception("verify two factor code error");
        }

        userRecord.setFlagTwoFactor(true);

        boolean updated  = userDb.update(userRecord);
        return updated;
    }

    @Override
    public boolean disableUserTwoFactor(Long userId, String code) throws Exception {
        UserRecord userRecord = userDb.getById(userId);
        if(null == userRecord){
            throw new Exception("no user");
        }

        String secret = userRecord.getTwoFactorSecret();
        if(StringUtil.isBlank(secret)){
            throw new Exception("no secret");
        }

        boolean passed = twoFactorBiz.verifyTwoFactorCode(secret, code);
        if(!passed){
            throw new Exception("verify two factor code error");
        }

        userRecord.setTwoFactorSecret(null);
        userRecord.setFlagTwoFactor(false);

        boolean updated  = userDb.update(userRecord);
        return updated;
    }


}
