package com.airxiechao.y20.manmachinetest.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.manmachinetest.biz.api.IManMachineBiz;
import com.airxiechao.y20.manmachinetest.pojo.ManMachineTestQuestion;
import com.airxiechao.y20.manmachinetest.rest.param.CheckManMachineQuestionParam;
import com.airxiechao.y20.manmachinetest.rest.api.IUserManMachineTestRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManMachineTestRestHandler implements IUserManMachineTestRest {

    private static final Logger logger = LoggerFactory.getLogger(UserManMachineTestRestHandler.class);
    private IManMachineBiz manMachineBiz = Biz.get(IManMachineBiz.class);

    @Override
    public Response nextQuestion(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        try {
            ManMachineTestQuestion question = manMachineBiz.randomQuestion();
            return new Response().data(question);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response checkQuestion(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CheckManMachineQuestionParam param = RestUtil.rawJsonData(exchange, CheckManMachineQuestionParam.class);
        try {
            boolean passed = manMachineBiz.checkQuestion(param.getToken(), param.getAnswer());
            if(!passed){
                return new Response().error();
            }

            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

    }
}
