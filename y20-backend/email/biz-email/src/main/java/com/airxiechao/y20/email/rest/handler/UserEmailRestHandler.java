package com.airxiechao.y20.email.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.pojo.vo.AccountVo;
import com.airxiechao.y20.auth.rest.api.IServiceAccountRest;
import com.airxiechao.y20.auth.rest.param.ServiceGetAccountParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.manmachinetest.rest.param.CheckManMachineQuestionParam;
import com.airxiechao.y20.manmachinetest.rest.api.IServiceManMachineTestRest;
import com.airxiechao.y20.email.biz.api.IEmailBiz;
import com.airxiechao.y20.email.cache.VerificationCodeEmailLimitCache;
import com.airxiechao.y20.email.rest.param.SendEmailVerificationCodeParam;
import com.airxiechao.y20.email.rest.api.IUserEmailRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEmailRestHandler implements IUserEmailRest {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailRestHandler.class);
    private IEmailBiz emailBiz = Biz.get(IEmailBiz.class);

    @Override
    public Response sendVerificationCode(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        SendEmailVerificationCodeParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, SendEmailVerificationCodeParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // check email
        if(StringUtil.isBlank(param.getEmail())){
            Response<AccountVo> accountResp = ServiceRestClient.get(IServiceAccountRest.class).getAccount(
                    new ServiceGetAccountParam(param.getUserId()));
            if(!accountResp.isSuccess()){
                return new Response().error(accountResp.getMessage());
            }

            String email = accountResp.getData().getEmail();
            if(StringUtil.isBlank(email)){
                return new Response().error("email is empty");
            }

            param.setEmail(email);
        }

        // check man-machine-test
        Response resp = ServiceRestClient.get(IServiceManMachineTestRest.class).checkQuestion(
                new CheckManMachineQuestionParam(param.getManMachineTestQuestionToken(), param.getManMachineTestAnswer())
        );

        if(!resp.isSuccess()){
            return new Response().error("man-machine-test fail");
        }

        // check email-limit
        if(VerificationCodeEmailLimitCache.getInstance().exists(param.getEmail())){
            return new Response().error("email-limit check fail");
        }

        try {
            String verificationCodeToken = emailBiz.sendVerificationCode(param.getEmail());
            VerificationCodeEmailLimitCache.getInstance().put(param.getEmail());
            return new Response().data(verificationCodeToken);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

}
