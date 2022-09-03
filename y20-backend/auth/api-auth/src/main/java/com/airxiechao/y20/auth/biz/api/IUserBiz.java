package com.airxiechao.y20.auth.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.pojo.vo.LoginVo;
import com.airxiechao.y20.auth.pojo.vo.TwoFactorSecretVo;

@IBiz
public interface IUserBiz {

    UserRecord getUser(Long userId);
    UserRecord getByMobile(String mobile);
    UserRecord getByEmail(String email);
    UserRecord getByUsername(String username);

    UserRecord createUser(
            String username,
            String password,
            String mobile);

    boolean validateUser(String username, String password);
    LoginVo loginUser(String username, String password, String ip) throws Exception;
    LoginVo loginUser(String username, String ip) throws Exception;

    boolean updateUserMobile(Long userId, String mobile) throws Exception;
    boolean updateUserEmail(Long userId, String email) throws Exception;
    boolean updateUserPassword(Long userId, String password) throws Exception;
    TwoFactorSecretVo createUserTwoFactorSecret(Long userId) throws Exception;
    boolean enableUserTwoFactor(Long userId, String code) throws Exception;
    boolean disableUserTwoFactor(Long userId, String code) throws Exception;
}
