package com.kyodream.debugger.service;

import kyodream.record.ContextRecord;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
@Component
@ServerEndpoint("/ws/data/context")
public class ContextWebSocket extends AbstractWS {
    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    public void sendContextData(ContextRecord contextRecord) {
        AnalystsInfo debugInfo = new AnalystsInfo(InfoType.Success, contextRecord);
        super.sendMsg(debugInfo);
    }
    @OnClose
    public void onClose(Session session) {
        super.close(session);
    }
}
