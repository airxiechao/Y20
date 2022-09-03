package com.airxiechao.y20.email.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.email.rest.api.IServiceEmailRest;
import com.airxiechao.y20.email.biz.api.IEmailBiz;
import com.airxiechao.y20.email.rest.param.CheckEmailVerificationCodeParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceEmailRestHandler implements IServiceEmailRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceEmailRestHandler.class);
    private IEmailBiz smsBiz = Biz.get(IEmailBiz.class);

    @Override
    public Response checkVerificationCode(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CheckEmailVerificationCodeParam param = RestUtil.rawJsonData(exchange, CheckEmailVerificationCodeParam.class);

        try {
            boolean passed = smsBiz.checkVerificationCode(param.getVerificationCodeToken(), param.getVerificationEmail(), param.getVerificationCode());
            if(!passed){
                return new Response().error();
            }

            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }
}
