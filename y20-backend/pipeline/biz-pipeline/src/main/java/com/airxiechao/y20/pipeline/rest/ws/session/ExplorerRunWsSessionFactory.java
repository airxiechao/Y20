package com.airxiechao.y20.pipeline.rest.ws.session;

import com.airxiechao.axcboot.communication.websocket.server.WsSessionFactory;
import com.airxiechao.axcboot.util.UuidUtil;
import io.undertow.websockets.core.WebSocketChannel;

public class ExplorerRunWsSessionFactory extends WsSessionFactory<ExplorerRunWsSession> {
    public ExplorerRunWsSession createSession(WebSocketChannel channel, Long pipelineRunId, String pipelineRunInstanceUuid, String explorerRunInstanceUuid){
        ExplorerRunWsSession session = new ExplorerRunWsSession(pipelineRunId, pipelineRunInstanceUuid, explorerRunInstanceUuid, UuidUtil.random());
        return super.createSession(channel, session);
    }
}
