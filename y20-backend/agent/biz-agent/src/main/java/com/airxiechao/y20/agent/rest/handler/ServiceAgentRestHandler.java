package com.airxiechao.y20.agent.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.agent.biz.api.IAgentBiz;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.agent.pojo.Agent;
import com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.api.IServiceAgentServerRest;
import com.airxiechao.y20.agent.rest.param.CountAgentParam;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.agent.rest.param.IsAgentActiveParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceAgentServerRestClient;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAgentRestHandler implements IServiceAgentRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAgentRestHandler.class);
    private IAgentBiz agentBiz = Biz.get(IAgentBiz.class);

    @Override
    public Response<AgentVo> getAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetAgentParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetAgentParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        AgentRecord agentRecord = agentBiz.getByUserIdAndAgentId(param.getUserId(), param.getAgentId());
        if(null == agentRecord){
            return new Response().error("no agent");
        }
        // agent vo
        AgentVo agentVo;
        Agent agent = agentRecord.toPojo();
        if(EnumAgentStatus.STATUS_OFFLINE.equals(agent.getStatus())){
            agentVo = new AgentVo(agent, false);
        }else{
            // check agent real status
            try{
                Response activeResp = ServiceAgentServerRestClient.get(IServiceAgentServerRest.class, agent.getClientId()).isAgentActive(
                        new IsAgentActiveParam(agent.getClientId()));
                agentVo = new AgentVo(agent, activeResp.isSuccess());
            }catch (Exception e){
                agentVo = new AgentVo(agent, false);
            }
        }

        return new Response<AgentVo>().data(agentVo);
    }

    @Override
    public Response<Long> countAgent(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CountAgentParam param = null;
        try {
            param = RestUtil.queryData(exchange, CountAgentParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Long count = agentBiz.count(param.getUserId(), null);
        return new Response<Long>().data(count);
    }
}
