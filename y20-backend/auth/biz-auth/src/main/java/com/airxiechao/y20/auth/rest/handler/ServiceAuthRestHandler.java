package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.db.record.AccessTokenRecord;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.auth.pojo.exception.InvalidAccessTokenException;
import com.airxiechao.y20.auth.rest.param.ExtractAccessPrincipalParam;
import com.airxiechao.y20.auth.rest.param.ServiceGetAccessTokenParam;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.common.core.biz.Biz;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAuthRestHandler implements IServiceAuthRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAuthRestHandler.class);
    private IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);

    @Override
    public Response<AccessTokenRecord>  getAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceGetAccessTokenParam param = RestUtil.queryData(exchange, ServiceGetAccessTokenParam.class);

        AccessTokenRecord accessTokenRecord = accessTokenBiz.getByAccessToken(
                param.getAccessToken());

        if(null == accessTokenRecord){
            return new Response().error();
        }

        return new Response().data(accessTokenRecord);
    }

    @Override
    public Response validateAccessToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ValidateAccessTokenParam param = RestUtil.rawJsonData(exchange, ValidateAccessTokenParam.class);

        boolean valid = accessTokenBiz.validateAccessToken(
                param.getAccessToken(),
                param.getScope(),
                param.getItem(),
                param.getMode());
        if(!valid){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response<AccessPrincipal> extractAccessPrincipal(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ExtractAccessPrincipalParam param = RestUtil.rawJsonData(exchange, ExtractAccessPrincipalParam.class);

        try {
            AccessPrincipal principal = accessTokenBiz.extractAccessPrincipal(param.getAccessToken());
            return new Response().data(principal);
        } catch (InvalidAccessTokenException e) {
            logger.error("extract access principal error", e);
            return new Response().error(e.getMessage());
        }
    }
}
