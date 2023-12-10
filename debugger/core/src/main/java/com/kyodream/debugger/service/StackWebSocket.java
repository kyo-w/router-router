package com.kyodream.debugger.service;

import com.kyodream.debugger.pojo.ApiStack;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws/stack")
public class StackWebSocket extends AbstractWS {
    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    public void sendStack(ApiStack apiStack) {
        super.sendMsg(new AnalystsInfo(InfoType.Success, apiStack));
    }

    @OnClose
    public void close(Session session) {
        super.close(session);
    }

}
