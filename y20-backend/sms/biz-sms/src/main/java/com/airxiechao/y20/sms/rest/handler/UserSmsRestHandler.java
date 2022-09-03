package com.airxiechao.y20.sms.rest.handler;

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
import com.airxiechao.y20.sms.biz.api.ISmsBiz;
import com.airxiechao.y20.sms.cache.VerificationCodeSmsLimitCache;
import com.airxiechao.y20.sms.rest.param.SendSmsVerificationCodeParam;
import com.airxiechao.y20.sms.rest.api.IUserSmsRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSmsRestHandler implements IUserSmsRest {

    private static final Logger logger = LoggerFactory.getLogger(UserSmsRestHandler.class);
    private ISmsBiz smsBiz = Biz.get(ISmsBiz.class);

    @Override
    public Response sendVerificationCode(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        SendSmsVerificationCodeParam param = null;
        try {
            try{
                param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, SendSmsVerificationCodeParam.class, true);
            }catch (Exception e){
                param = RestUtil.rawJsonData(exchange, SendSmsVerificationCodeParam.class);
            }
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // check mobile
        if(StringUtil.isBlank(param.getMobile())){
            if(null == param.getUserId()){
                return new Response().error("mobile and userId are both empty");
            }

            Response<AccountVo> accountResp = ServiceRestClient.get(IServiceAccountRest.class).getAccount(
                    new ServiceGetAccountParam(param.getUserId()));
            if(!accountResp.isSuccess()){
                return new Response().error(accountResp.getMessage());
            }

            String mobile = accountResp.getData().getMobile();
            if(StringUtil.isBlank(mobile)){
                return new Response().error("mobile is empty");
            }

            param.setMobile(mobile);
        }

        // check man-machine-test
        Response resp = ServiceRestClient.get(IServiceManMachineTestRest.class).checkQuestion(
                new CheckManMachineQuestionParam(param.getManMachineTestQuestionToken(), param.getManMachineTestAnswer())
        );

        if(!resp.isSuccess()){
            return new Response().error("man-machine-test fail");
        }

        // check sms-limit
        if(VerificationCodeSmsLimitCache.getInstance().exists(param.getMobile())){
            return new Response().error("sms-limit check fail");
        }

        try {
            String verificationCodeToken = smsBiz.sendVerificationCode(param.getMobile());
            VerificationCodeSmsLimitCache.getInstance().put(param.getMobile());
            return new Response().data(verificationCodeToken);
        } catch (Exception e) {
            logger.error("send verification code sms error", e);
            return new Response().error(e.getMessage());
        }
    }

}
