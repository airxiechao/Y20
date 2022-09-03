package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.pipeline.biz.api.ITerminalRunInstanceBiz;
import com.airxiechao.y20.pipeline.rest.api.IServiceTerminalRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.CreateTerminalRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.param.DestroyTerminalRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.param.InputTerminalRunInstanceParam;
import com.airxiechao.y20.pipeline.run.terminal.ITerminalRunInstance;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTerminalRunInstanceRestHandler implements IServiceTerminalRunInstanceRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTerminalRunInstanceRestHandler.class);
    private ITerminalRunInstanceBiz terminalRunInstanceBiz = Biz.get(ITerminalRunInstanceBiz.class);

    @Override
    public Response<String> createTerminalRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateTerminalRunInstanceParam param = RestUtil.rawJsonData(exchange, CreateTerminalRunInstanceParam.class);

        try {
            ITerminalRunInstance terminalRunInstance = terminalRunInstanceBiz.createTerminalRunInstance(param.getPipelineRunInstanceUuid());
            return new Response().data(terminalRunInstance.getTerminalRunInstanceUuid());
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response destroyTerminalRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DestroyTerminalRunInstanceParam param = RestUtil.rawJsonData(exchange, DestroyTerminalRunInstanceParam.class);

        try {
            terminalRunInstanceBiz.destroyTerminalRunInstance(param.getPipelineRunInstanceUuid(), param.getTerminalRunInstanceUuid());
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
        return new Response();
    }

    @Override
    public Response inputTerminalRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        InputTerminalRunInstanceParam param = RestUtil.rawJsonData(exchange, InputTerminalRunInstanceParam.class);

        try {
            terminalRunInstanceBiz.inputTerminalRunInstance(param.getPipelineRunInstanceUuid(), param.getTerminalRunInstanceUuid(), param.getMessage());
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
        return new Response();
    }
}
