package com.airxiechao.y20.manmachinetest.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.manmachinetest.biz.api.IManMachineBiz;
import com.airxiechao.y20.manmachinetest.rest.param.CheckManMachineQuestionParam;
import com.airxiechao.y20.manmachinetest.rest.api.IServiceManMachineTestRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManMachineTestRestHandler implements IServiceManMachineTestRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManMachineTestRestHandler.class);
    private IManMachineBiz manMachineBiz = Biz.get(IManMachineBiz.class);

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
