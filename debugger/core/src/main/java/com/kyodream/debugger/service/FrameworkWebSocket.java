package com.kyodream.debugger.service;

import kyodream.record.FrameworkRecord;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws/data/framework")
public class FrameworkWebSocket extends AbstractWS{
    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    public void sendFrameworkData(FrameworkRecord contextRecord) {
        AnalystsInfo debugInfo = new AnalystsInfo(AbstractWS.InfoType.Success, contextRecord);
        super.sendMsg(debugInfo);
    }

    @OnClose
    public void onClose(Session session) {
        super.close(session);
    }
}
