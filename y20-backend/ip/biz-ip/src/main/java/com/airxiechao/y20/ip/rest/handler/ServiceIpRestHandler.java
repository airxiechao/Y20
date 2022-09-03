package com.airxiechao.y20.ip.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.ip.biz.api.IIpBiz;
import com.airxiechao.y20.ip.pojo.IpLocation;
import com.airxiechao.y20.ip.rest.api.IServiceIpRest;
import com.airxiechao.y20.ip.rest.param.ServiceResolveIpParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceIpRestHandler implements IServiceIpRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceIpRestHandler.class);
    private IIpBiz ipBiz = Biz.get(IIpBiz.class);

    @Override
    public Response<IpLocation> resolve(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceResolveIpParam param = RestUtil.queryData(exchange, ServiceResolveIpParam.class);

        try {
            IpLocation ipLocation = ipBiz.resolve(param.getIp());
            return new Response<IpLocation>().data(ipLocation);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

    }
}
