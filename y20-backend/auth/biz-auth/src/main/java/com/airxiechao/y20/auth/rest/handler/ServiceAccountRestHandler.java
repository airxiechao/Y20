package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.auth.biz.api.IUserBiz;
import com.airxiechao.y20.auth.db.record.UserRecord;
import com.airxiechao.y20.auth.pojo.vo.AccountVo;
import com.airxiechao.y20.auth.rest.api.IServiceAccountRest;
import com.airxiechao.y20.auth.rest.param.ServiceGetAccountParam;
import com.airxiechao.y20.common.core.biz.Biz;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAccountRestHandler implements IServiceAccountRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAccountRestHandler.class);
    private IUserBiz userBiz = Biz.get(IUserBiz.class);

    @Override
    public Response<AccountVo> getAccount(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ServiceGetAccountParam param = null;
        try {
            param = RestUtil.queryData(exchange, ServiceGetAccountParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        UserRecord userRecord = userBiz.getUser(param.getUserId());
        if(null == userRecord){
            return new Response().error("no user");
        }

        return new Response<AccountVo>().data(new AccountVo(userRecord));
    }
}
