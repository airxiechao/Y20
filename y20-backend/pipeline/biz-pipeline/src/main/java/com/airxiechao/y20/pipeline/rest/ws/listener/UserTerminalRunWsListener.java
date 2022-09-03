package com.airxiechao.y20.pipeline.rest.ws.listener;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.communication.websocket.annotation.WsEndpoint;
import com.airxiechao.axcboot.communication.websocket.common.AbstractWsRouterListener;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.EventPipelineTerminalRunOutput;
import com.airxiechao.y20.pipeline.rest.api.IServiceTerminalRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.InputTerminalRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.ws.param.TerminalRunWsParam;
import com.airxiechao.y20.pipeline.rest.ws.session.TerminalRunWsSession;
import com.airxiechao.y20.pipeline.rest.ws.session.TerminalRunWsSessionFactory;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WsEndpoint("/ws/user/terminal/run/attach")
public class UserTerminalRunWsListener extends AbstractWsRouterListener {
    private static final Logger logger = LoggerFactory.getLogger(UserTerminalRunWsListener.class);

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);
    private TerminalRunWsSessionFactory sessionFactory = new TerminalRunWsSessionFactory();

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {

        TerminalRunWsParam param;
        try {
            param = EnhancedRestUtil.queryWsDataWithHeader(exchange, TerminalRunWsParam.class, true);
        } catch (Exception e) {
            logger.error("parse ws param error", e);
            return;
        }

        // check ownership
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            logger.error("ws connect error: no pipeline run");
            return;
        }

        Long pipelineRunId = param.getPipelineRunId();
        String pipelineRunInstanceUuid = pipelineRunRecord.getInstanceUuid();
        String terminalRunInstanceUuid = param.getTerminalRunInstanceUuid();
        TerminalRunWsSession session = sessionFactory.createSession(channel, pipelineRunId, pipelineRunInstanceUuid, terminalRunInstanceUuid);

        logger.info("terminal run [{}] attache ws connect", session.getName());

        // bind terminal output event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineTerminalRunOutput.type(pipelineRunId, terminalRunInstanceUuid), session.getName(), map -> {
                    EventPipelineTerminalRunOutput event = ModelUtil.fromMap(map, EventPipelineTerminalRunOutput.class);
                    RestUtil.sendWsText(event.getOutput(), channel, null);

                    return new Response();
                }
        );

    }

    @Override
    public void onMessage(WebSocketChannel channel, String message) {
        TerminalRunWsSession session = sessionFactory.getSession(channel);

        try {
            Response resp = ServicePipelineRunInstanceRestClient.get(IServiceTerminalRunInstanceRest.class, session.getPipelineRunInstanceUuid()).inputTerminalRunInstance(
                    new InputTerminalRunInstanceParam(session.getPipelineRunInstanceUuid(), session.getTerminalRunInstanceUuid(), message));
            if(!resp.isSuccess()){
                logger.error("input terminal run {} error: {}", session.getName(), resp.getMessage());
            }
        } catch (Exception e) {
            logger.error("input terminal run {} error", session.getName(), e);
        }
    }

    @Override
    public void onError(WebSocketChannel webSocketChannel) {

    }

    @Override
    public void onClose(WebSocketChannel webSocketChannel) {
        TerminalRunWsSession session = sessionFactory.getSession(webSocketChannel);

        logger.info("terminal run [{}] ws attach close", session.getName());

        // unsubscribe terminal run output event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineTerminalRunOutput.type(session.getPipelineRunId(), session.getTerminalRunInstanceUuid()), session.getName());

        sessionFactory.destroySession(webSocketChannel);
    }
}
