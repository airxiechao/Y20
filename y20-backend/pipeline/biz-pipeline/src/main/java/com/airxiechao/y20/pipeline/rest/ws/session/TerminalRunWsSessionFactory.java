package com.airxiechao.y20.pipeline.rest.ws.session;

import com.airxiechao.axcboot.communication.websocket.server.WsSessionFactory;
import com.airxiechao.axcboot.util.UuidUtil;
import io.undertow.websockets.core.WebSocketChannel;

public class TerminalRunWsSessionFactory extends WsSessionFactory<TerminalRunWsSession> {
    public TerminalRunWsSession createSession(WebSocketChannel channel, Long pipelineRunId, String pipelineRunInstanceUuid, String terminalRunInstanceUuid){
        TerminalRunWsSession session = new TerminalRunWsSession(pipelineRunId, pipelineRunInstanceUuid, terminalRunInstanceUuid, UuidUtil.random());
        return super.createSession(channel, session);
    }
}