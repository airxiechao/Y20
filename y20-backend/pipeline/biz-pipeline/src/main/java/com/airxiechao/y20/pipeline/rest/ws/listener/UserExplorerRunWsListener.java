package com.airxiechao.y20.pipeline.rest.ws.listener;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.communication.websocket.annotation.WsEndpoint;
import com.airxiechao.axcboot.communication.websocket.common.AbstractWsRouterListener;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.EventPipelineExplorerRunTransferProgress;
import com.airxiechao.y20.pipeline.rest.ws.param.ExplorerRunWsParam;
import com.airxiechao.y20.pipeline.rest.ws.session.ExplorerRunWsSession;
import com.airxiechao.y20.pipeline.rest.ws.session.ExplorerRunWsSessionFactory;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WsEndpoint("/ws/user/explorer/run/attach")
public class UserExplorerRunWsListener extends AbstractWsRouterListener {
    private static final Logger logger = LoggerFactory.getLogger(UserExplorerRunWsListener.class);

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);
    private ExplorerRunWsSessionFactory sessionFactory = new ExplorerRunWsSessionFactory();

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {

        ExplorerRunWsParam param;
        try {
            param = EnhancedRestUtil.queryWsDataWithHeader(exchange, ExplorerRunWsParam.class, true);
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
        String explorerRunInstanceUuid = param.getExplorerRunInstanceUuid();
        ExplorerRunWsSession session = sessionFactory.createSession(channel, pipelineRunId, pipelineRunInstanceUuid, explorerRunInstanceUuid);

        logger.info("explorer run [{}] attache ws connect", session.getName());

        // bind explorer transfer progress event
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineExplorerRunTransferProgress.type(pipelineRunId, explorerRunInstanceUuid), session.getName(), map -> {
                    map.put("type", EnumPipelineEventType.PIPELINE_EXPLORER_RUN_TRANSFER_PROGRESS);
                    RestUtil.sendWsObject(map, channel, null);

                    return new Response();
                }
        );

    }

    @Override
    public void onMessage(WebSocketChannel channel, String message) {

    }

    @Override
    public void onError(WebSocketChannel webSocketChannel) {

    }

    @Override
    public void onClose(WebSocketChannel webSocketChannel) {
        ExplorerRunWsSession session = sessionFactory.getSession(webSocketChannel);

        logger.info("explorer run [{}] ws attach close", session.getName());

        // unsubscribe explorer run output event
        EventBus.getInstance().getPubSub().unsubscribe(EventPipelineExplorerRunTransferProgress.type(session.getPipelineRunId(), session.getExplorerRunInstanceUuid()), session.getName());

        sessionFactory.destroySession(webSocketChannel);
    }
}
