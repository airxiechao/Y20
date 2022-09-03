package com.airxiechao.y20.pipeline.rest.ws.session;

import com.airxiechao.axcboot.communication.websocket.server.WsSessionFactory;
import com.airxiechao.axcboot.util.UuidUtil;
import io.undertow.websockets.core.WebSocketChannel;

public class PipelineRunWsSessionFactory extends WsSessionFactory<PipelineRunWsSession> {
    public PipelineRunWsSession createSession(WebSocketChannel channel, Long pipelineRunId){
        PipelineRunWsSession session = new PipelineRunWsSession(pipelineRunId, UuidUtil.random());
        return super.createSession(channel, session);
    }
}
