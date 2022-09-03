package com.airxiechao.y20.sms.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.sms.biz.api.ISmsBiz;
import com.airxiechao.y20.sms.rest.param.CheckSmsVerificationCodeParam;
import com.airxiechao.y20.sms.rest.api.IServiceSmsRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceSmsRestHandler implements IServiceSmsRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSmsRestHandler.class);
    private ISmsBiz smsBiz = Biz.get(ISmsBiz.class);

    @Override
    public Response checkVerificationCode(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CheckSmsVerificationCodeParam param = RestUtil.rawJsonData(exchange, CheckSmsVerificationCodeParam.class);

        try {
            boolean passed = smsBiz.checkVerificationCode(param.getVerificationCodeToken(), param.getVerificationMobile(), param.getVerificationCode());
            if(!passed){
                return new Response().error();
            }

            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

}
