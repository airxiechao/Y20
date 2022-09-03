package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.biz.api.ITwoFactorBiz;
import com.airxiechao.y20.auth.biz.api.IUserBiz;
import com.airxiechao.y20.auth.cache.LoginByMobileFailLimitCache;
import com.airxiechao.y20.auth.cache.LoginByUsernameFailLimitCache;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.pojo.TwoFactorPrincipal;
import com.airxiechao.y20.auth.pojo.vo.AccessTokenVo;
import com.airxiechao.y20.auth.rest.param.*;
import com.airxiechao.y20.auth.pojo.vo.LoginVo;
import com.airxiechao.y20.auth.rest.api.IUserAuthRest;
import com.airxiechao.y20.auth.util.AuthRestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.constant.code.EnumRespCode;
import com.airxiechao.y20.ip.pojo.IpLocation;
import com.airxiechao.y20.ip.rest.api.IServiceIpRest;
import com.airxiechao.y20.ip.rest.param.ServiceResolveIpParam;
import com.airxiechao.y20.sms.rest.param.CheckSmsVerificationCodeParam;
import com.airxiechao.y20.sms.rest.api.IServiceSmsRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserAuthRestHandler implements IUserAuthRest {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthRestHandler.class);
    private IUserBiz userBiz = Biz.get(IUserBiz.class);
    private IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);
    private ITwoFactorBiz twoFactorBiz = Biz.get(ITwoFactorBiz.class);

    @Override
    public Response loginByUsername(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        LoginByUsernameParam loginParam = RestUtil.rawJsonData(exchange, LoginByUsernameParam.class);
        String username = loginParam.getUsername();
        String password = loginParam.getPassword();
        String ip = RestUtil.getRemoteIp(exchange);

        // fail limit check
        if(LoginByUsernameFailLimitCache.getInstance().exceeds(username)){
            return new Response().error("fail-limit check fail");
        }

        // check password
        boolean passwordPassed = userBiz.validateUser(username, password);
        if(!passwordPassed){
            return new Response().error("username or password error");
        }

        // two factor check
        UserRecord userRecord = userBiz.getByUsername(username);
        if(null == userRecord){
            return new Response().error("no user");
        }
        if(null != userRecord.getFlagTwoFactor() && userRecord.getFlagTwoFactor()){
            Response errResp = new Response().error();
            errResp.setCode(EnumRespCode.ERROR_NEED_TWO_FACTOR);

            String twoFactorToken = twoFactorBiz.createTwoFactorToken(userRecord.getId());
            errResp.setData(twoFactorToken);
            return errResp;
        }

        try {
            LoginVo loginVo = userBiz.loginUser(username, password, ip);
            return new Response().data(loginVo);
        } catch (Exception e) {
            logger.error("login user error", e);
            LoginByUsernameFailLimitCache.getInstance().add(username);

            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response loginByMobile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        LoginByMobileParam loginParam = RestUtil.rawJsonData(exchange, LoginByMobileParam.class);
        String mobile = loginParam.getMobile();
        String ip = RestUtil.getRemoteIp(exchange);

        // fail limit check
        if(LoginByMobileFailLimitCache.getInstance().exceeds(mobile)){
            return new Response().error("fail-limit check fail");
        }

        // get user
        UserRecord userRecord = userBiz.getByMobile(mobile);
        if(null == userRecord){
            return new Response().error("no user");
        }

        // check verification code
        Response resp = ServiceRestClient.get(IServiceSmsRest.class).checkVerificationCode(
                new CheckSmsVerificationCodeParam(loginParam.getVerificationCodeToken(), userRecord.getMobile(), loginParam.getVerificationCode())
        );
        if(!resp.isSuccess()){
            LoginByMobileFailLimitCache.getInstance().add(mobile);
            return new Response().error("check verification code fail");
        }

        // two factor check
        if(null != userRecord.getFlagTwoFactor() && userRecord.getFlagTwoFactor()){
            Response errResp = new Response().error();
            errResp.setCode(EnumRespCode.ERROR_NEED_TWO_FACTOR);

            String twoFactorToken = twoFactorBiz.createTwoFactorToken(userRecord.getId());
            errResp.setData(twoFactorToken);
            return errResp;
        }

        // login user
        try {
            LoginVo loginVo = userBiz.loginUser(userRecord.getUsername(), ip);
            return new Response().data(loginVo);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response loginByTwoFactor(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        LoginByTwoFactorParam loginParam = RestUtil.rawJsonData(exchange, LoginByTwoFactorParam.class);
        String twoFactorToken = loginParam.getTwoFactorToken();
        String twoFactorCode = loginParam.getTwoFactorCode();
        String ip = RestUtil.getRemoteIp(exchange);

        TwoFactorPrincipal twoFactorPrincipal;
        try {
            twoFactorPrincipal = twoFactorBiz.extractTwoFactorPrincipal(twoFactorToken);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get user
        UserRecord userRecord = userBiz.getUser(twoFactorPrincipal.getTwoFactorUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        // check two factor code
        boolean twoFactorPassed = twoFactorBiz.verifyTwoFactorCode(userRecord.getTwoFactorSecret(), twoFactorCode);
        if(!twoFactorPassed){
            return new Response().error("invalid two factor code");
        }

        // login user
        try {
            LoginVo loginVo = userBiz.loginUser(userRecord.getUsername(), ip);
            return new Response().data(loginVo);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response logout(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        LogoutParam param;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, LogoutParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AccessTokenRecord accessTokenRecord = accessTokenBiz.getByAccessToken(param.getAccessToken());
        if(null == accessTokenRecord){
            return new Response().error("no access token record");
        }

        boolean deleted = accessTokenBiz.deleteAccessToken(accessTokenRecord.getId());
        if(!deleted){
            return new Response().error("delete access token fail");
        }

        return new Response();
    }

    @Override
    public Response signup(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        SignupParam param = RestUtil.rawJsonData(exchange, SignupParam.class);

        // check verification code
        Response resp = ServiceRestClient.get(IServiceSmsRest.class).checkVerificationCode(
                new CheckSmsVerificationCodeParam(param.getVerificationCodeToken(), param.getMobile(), param.getVerificationCode())
        );
        if(!resp.isSuccess()){
            return new Response().error("check verification code fail");
        }

        // check username exists
        UserRecord userRecord = userBiz.getByUsername(param.getUsername());
        if(null != userRecord){
            return new Response().error("username exists");
        }

        // check mobile exists
        userRecord = userBiz.getByMobile(param.getMobile());
        if(null != userRecord){
            return new Response().error("mobile exists");
        }

        // check username format
        boolean validFormat = validateUsernameFormat(param.getUsername());
        if(!validFormat){
            return new Response().error("invalid username format");
        }

        // create user
        userRecord = userBiz.createUser(
                param.getUsername(),
                param.getPassword(),
                param.getMobile());

        return new Response();
    }

    @Override
    public Response listUserAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListUserAccessTokenParam param;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, ListUserAccessTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        long count = accessTokenBiz.countUserAccessToken(param.getUserId());

        List<AccessTokenRecord> records = accessTokenBiz.listUserAccessToken(
                param.getUserId(), param.getOrderField(), param.getOrderType(), param.getPageNo(), param.getPageSize()
        );

        // update ip location
        records.forEach(record -> {
            String createIp = record.getCreateIp();
            String createLocation = record.getCreateLocation();
            if(!StringUtil.isBlank(createIp) && StringUtil.isBlank(createLocation)){
                Response<IpLocation> ipResp = ServiceRestClient.get(IServiceIpRest.class).resolve(
                        new ServiceResolveIpParam(createIp)
                );

                if(ipResp.isSuccess()){
                    IpLocation ipLocation = ipResp.getData();
                    record.setCreateLocation(String.format("%s%s%s",
                            StringUtil.notNull(ipLocation.getProvince()),
                            StringUtil.notNull(ipLocation.getCity()),
                            StringUtil.notNull(ipLocation.getDistrict()))
                    );

                    boolean updated = accessTokenBiz.updateAccessToken(record);
                    if(!updated){
                        logger.error("update access token location error");
                    }
                }
            }
        });

        List<AccessTokenVo> list = records.stream()
                .map(record -> {
                    boolean flagCurrentSession = accessTokenBiz.hashAccessToken(param.getAccessToken()).equals(record.getTokenHashed());
                    return new AccessTokenVo(record, flagCurrentSession);
                }).collect(Collectors.toList());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response listNotUserAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListNotUserAccessTokenParam param;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, ListNotUserAccessTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        long count = accessTokenBiz.countNotUserAccessToken(param.getUserId(), param.getName());

        List<AccessTokenVo> list = accessTokenBiz.listNotUserAccessToken(
                param.getUserId(), param.getName(), param.getOrderField(), param.getOrderType(), param.getPageNo(), param.getPageSize())
                .stream()
                .map(record -> new AccessTokenVo(record)).collect(Collectors.toList());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response createAgentAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateAgentAccessTokenParam param;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, CreateAgentAccessTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String ip = RestUtil.getRemoteIp(exchange);

        String agentAccessToken = accessTokenBiz.createAgentAccessToken(
                param.getUserId(), param.getName(), param.getBeginTime(), param.getEndTime(), ip);

        return new Response().data(agentAccessToken);
    }

    @Override
    public Response createWebhookAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateAgentAccessTokenParam param;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, CreateAgentAccessTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String ip = RestUtil.getRemoteIp(exchange);

        String agentAccessToken = accessTokenBiz.createWebhookAccessToken(
                param.getUserId(), param.getName(), param.getBeginTime(), param.getEndTime(), ip);

        return new Response().data(agentAccessToken);
    }

    @Override
    public Response deleteAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeleteAccessTokenParam param;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, DeleteAccessTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AccessTokenRecord accessTokenRecord = accessTokenBiz.getByIdAndUserId(param.getAccessTokenId(), param.getUserId());
        if(null == accessTokenRecord){
            return new Response().error("no access token");
        }

        boolean deleted = accessTokenBiz.deleteAccessToken(accessTokenRecord.getId());
        if(!deleted){
            return new Response().error("delete access token error");
        }

        return new Response();
    }

    private boolean validateUsernameFormat(String username){
        return username.matches("^[a-zA-Z0-9-_]{3,20}$");
    }
}
