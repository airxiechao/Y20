package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.biz.api.IUserBiz;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.pojo.config.AuthConfig;
import com.airxiechao.y20.auth.pojo.vo.AccountVo;
import com.airxiechao.y20.auth.pojo.vo.TwoFactorSecretVo;
import com.airxiechao.y20.auth.rest.api.IUserAccountRest;
import com.airxiechao.y20.auth.rest.param.*;
import com.airxiechao.y20.auth.util.AuthRestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.email.rest.api.IServiceEmailRest;
import com.airxiechao.y20.email.rest.param.CheckEmailVerificationCodeParam;
import com.airxiechao.y20.sms.rest.api.IServiceSmsRest;
import com.airxiechao.y20.sms.rest.param.CheckSmsVerificationCodeParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAccountRestHandler implements IUserAccountRest {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountRestHandler.class);
    private static AuthConfig authConfig = ConfigFactory.get(AuthConfig.class);
    private IUserBiz userBiz = Biz.get(IUserBiz.class);

    @Override
    public Response getAccount(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetAccountParam param = null;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, GetAccountParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        return new Response().data(new AccountVo(userRecord));
    }

    @Override
    public Response updateAccountMobile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateAccountMobileParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, UpdateAccountMobileParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }
        String mobile = param.getMobile();

        // check verification code
        Response resp = ServiceRestClient.get(IServiceSmsRest.class).checkVerificationCode(
                new CheckSmsVerificationCodeParam(param.getVerificationCodeToken(), mobile, param.getVerificationCode())
        );
        if(!resp.isSuccess()){
            return new Response().error("check verification code fail");
        }

        UserRecord userRecord = userBiz.getByMobile(mobile);
        if(null != userRecord){
            return new Response().error("mobile exists");
        }

        try {
            boolean updated = userBiz.updateUserMobile(param.getUserId(), mobile);
            if(!updated){
                return new Response().error("update user mobile error");
            }

            return new Response();
        } catch (Exception e) {
            logger.error("update user mobile error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response updateAccountEmail(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateAccountEmailParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, UpdateAccountEmailParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }
        String email = param.getEmail();

        // check verification code
        Response resp = ServiceRestClient.get(IServiceEmailRest.class).checkVerificationCode(
                new CheckEmailVerificationCodeParam(param.getVerificationCodeToken(), email, param.getVerificationCode())
        );
        if(!resp.isSuccess()){
            return new Response().error("check verification code fail");
        }

        UserRecord userRecord = userBiz.getByEmail(email);
        if(null != userRecord){
            return new Response().error("email exists");
        }

        try {
            boolean updated = userBiz.updateUserEmail(param.getUserId(), email);
            if(!updated){
                return new Response().error("update user email error");
            }

            return new Response();
        } catch (Exception e) {
            logger.error("update user email error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response updateAccountPassword(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateAccountPasswordParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, UpdateAccountPasswordParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // get user
        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        if(authConfig.isEnableSmsVerificationCode()) {
            // check verification code
            if(StringUtil.isBlank(param.getVerificationCodeToken()) ||
                StringUtil.isBlank(param.getVerificationCode())
            ){
                return new Response().error("check verification code fail");
            }

            Response resp = ServiceRestClient.get(IServiceSmsRest.class).checkVerificationCode(
                    new CheckSmsVerificationCodeParam(param.getVerificationCodeToken(), userRecord.getMobile(), param.getVerificationCode())
            );

            if (!resp.isSuccess()) {
                return new Response().error("check verification code fail");
            }
        }

        try {
            boolean updated = userBiz.updateUserPassword(param.getUserId(), param.getPassword());
            if(!updated){
                return new Response().error("update user password error");
            }

            return new Response();
        } catch (Exception e) {
            logger.error("update user password error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response createAccountTwoFactorSecret(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateAccountTwoFactorSecretParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, CreateAccountTwoFactorSecretParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        if(null != userRecord.getFlagTwoFactor() && userRecord.getFlagTwoFactor()){
            return new Response().error("two factor already enabled");
        }

        // create two factor secret
        try {
            TwoFactorSecretVo twoFactorSecretVo = userBiz.createUserTwoFactorSecret(param.getUserId());
            return new Response().data(twoFactorSecretVo);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response enableAccountTwoFactor(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        EnableAccountTwoFactorParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, EnableAccountTwoFactorParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        if(null != userRecord.getFlagTwoFactor() && userRecord.getFlagTwoFactor()){
            return new Response().error("two factor already enabled");
        }

        if(StringUtil.isBlank(userRecord.getTwoFactorSecret())){
            return new Response().error("no two factor secret");
        }

        // enable two factor secret

        try {
            boolean enabled = userBiz.enableUserTwoFactor(param.getUserId(), param.getCode());
            if(!enabled){
                throw new Exception("enable two factor error");
            }

            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response disableAccountTwoFactor(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DisableAccountTwoFactorParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, DisableAccountTwoFactorParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        if(null == userRecord.getFlagTwoFactor() || !userRecord.getFlagTwoFactor()){
            return new Response().error("two factor already disabled");
        }

        // disable two factor secret

        try {
            boolean disabled = userBiz.disableUserTwoFactor(param.getUserId(), param.getCode());
            if(!disabled){
                throw new Exception("disable two factor error");
            }

            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }
}
