package com.airxiechao.y20.monitor.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.monitor.biz.api.IMonitorBiz;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.ListMonitorParam;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorBasicParam;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorStatusParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AgentMonitorRestHandler implements IAgentMonitorRest {

    private static final Logger logger = LoggerFactory.getLogger(AgentMonitorRestHandler.class);

    private IMonitorBiz monitorBiz = Biz.get(IMonitorBiz.class);

    @Override
    public Response<List<Monitor>> list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListMonitorParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListMonitorParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // list
        List<Monitor> list = monitorBiz.list(
                param.getUserId(),
                param.getProjectId(),
                param.getAgentId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> record.toPojo()).collect(Collectors.toList());

        return new Response<List<Monitor>>().data(list);
    }

    @Override
    public Response updateStatus(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateMonitorStatusParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateMonitorStatusParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = monitorBiz.updateStatus(
                param.getUserId(), param.getProjectId(), param.getMonitorId(), param.getStatus());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

}
