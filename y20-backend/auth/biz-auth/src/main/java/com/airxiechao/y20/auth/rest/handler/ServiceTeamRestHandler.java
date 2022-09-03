package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.auth.biz.api.ITeamBiz;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;
import com.airxiechao.y20.auth.rest.api.IServiceTeamRest;
import com.airxiechao.y20.auth.rest.param.ServiceGetJoinedTeamParam;
import com.airxiechao.y20.common.core.biz.Biz;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTeamRestHandler implements IServiceTeamRest {

    private static Logger logger = LoggerFactory.getLogger(ServiceTeamRestHandler.class);
    private ITeamBiz teamBiz = Biz.get(ITeamBiz.class);

    @Override
    public Response<JoinedTeamVo> getJoinedTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ServiceGetJoinedTeamParam param = null;
        try {
            param = RestUtil.queryData(exchange, ServiceGetJoinedTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        JoinedTeamVo joinedTeamVo = teamBiz.getJoinedTeam(param.getMemberUserId(), param.getJoinedTeamId());
        if(null == joinedTeamVo){
            return new Response().error("no joined team");
        }

        return new Response<JoinedTeamVo>().data(joinedTeamVo);
    }
}
