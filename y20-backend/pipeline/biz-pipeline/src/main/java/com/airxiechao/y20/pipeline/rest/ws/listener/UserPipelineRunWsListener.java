package com.airxiechao.y20.pipeline.rest.ws.listener;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.communication.websocket.annotation.WsEndpoint;
import com.airxiechao.axcboot.communication.websocket.common.AbstractWsRouterListener;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.*;
import com.airxiechao.y20.pipeline.rest.ws.param.PipelineRunWsParam;
import com.airxiechao.y20.pipeline.rest.ws.session.PipelineRunWsSession;
import com.airxiechao.y20.pipeline.rest.ws.session.PipelineRunWsSessionFactory;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WsEndpoint("/ws/user/pipeline/run/attach")
public class UserPipelineRunWsListener extends AbstractWsRouterListener {

    private static final Logger logger = LoggerFactory.getLogger(UserPipelineRunWsListener.class);

    private PipelineRunWsSessionFactory sessionFactory = new PipelineRunWsSessionFactory();

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {

        PipelineRunWsParam param;
        try {
            param = EnhancedRestUtil.queryWsDataWithHeader(exchange, PipelineRunWsParam.class, true);
        } catch (Exception e) {
            logger.error("ws connect error", e);
            return;
        }

        // check ownership
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            logger.error("ws connect error: no pipeline run");
            return;
        }

        Long pipelineRunId = param.getPipelineRunId();
        PipelineRunWsSession session = sessionFactory.createSession(channel, pipelineRunId);

        logger.info("pipeline run [{}] attach ws connect", session.getName());

        // bind pipeline run status update event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineRunStatusUpdate.type(pipelineRunId), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_RUN_STATUS_UPDATE);
                    Response resp = new Response().data(map);

                    RestUtil.sendWsObject(resp, channel, null);

                    return new Response();
                }
        );

        // bind step status update event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineStepRunStatusUpdate.type(pipelineRunId, null), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_STEP_RUN_STATUS_UPDATE);

                    Response resp = new Response().data(map);

                    RestUtil.sendWsObject(resp, channel, null);

                    return new Response();
                }
        );

        // bind step output event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineStepRunOutput.type(pipelineRunId, null), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_STEP_RUN_OUTPUT);

                    Response resp = new Response().data(map);

                    RestUtil.sendWsObject(resp, channel, null);

                    return new Response();
                }
        );

        // bind terminal status update event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineTerminalRunStatusUpdate.type(pipelineRunId, null), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_TERMINAL_RUN_STATUS_UPDATE);

                    Response resp = new Response().data(map);

                    RestUtil.sendWsObject(resp, channel, null);

                    return new Response();
                }
        );

        // bind explorer status update event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineExplorerRunStatusUpdate.type(pipelineRunId, null), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_EXPLORER_RUN_STATUS_UPDATE);

                    Response resp = new Response().data(map);

                    RestUtil.sendWsObject(resp, channel, null);

                    return new Response();
                }
        );
    }

    @Override
    public void onClose(WebSocketChannel webSocketChannel) {
        PipelineRunWsSession session = sessionFactory.getSession(webSocketChannel);

        logger.info("pipeline run [{}] attach ws close", session.getName());

        // unsubscribe pipeline status update event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineRunStatusUpdate.type(session.getPipelineRunId()), session.getName());

        // unsubscribe step status update event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineStepRunStatusUpdate.type(session.getPipelineRunId(), null), session.getName());

        // unsubscribe step output event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineStepRunOutput.type(session.getPipelineRunId(), null), session.getName());

        // unsubscribe terminal status update event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineTerminalRunStatusUpdate.type(session.getPipelineRunId(), null), session.getName());

        // unsubscribe explorer status update event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineExplorerRunStatusUpdate.type(session.getPipelineRunId(), null), session.getName());


        sessionFactory.destroySession(webSocketChannel);
    }

    @Override
    public void onError(WebSocketChannel webSocketChannel) {

    }
}
