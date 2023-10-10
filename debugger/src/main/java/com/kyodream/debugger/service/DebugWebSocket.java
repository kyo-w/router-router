package com.kyodream.debugger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyodream.debugger.pojo.DebugInfo;
import com.kyodream.debugger.pojo.DebugType;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/debug/info")
public class DebugWebSocket {
    private static CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    public void sendMsg(DebugInfo debugInfo) {
        debugInfo.setDate(new Date());
        ObjectMapper mapper = new ObjectMapper();
        String message = "";
        try {
            message = mapper.writeValueAsString(debugInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                synchronized (session) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnClose
    public void close(Session session) {
        try {
            SESSIONS.remove(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendInfo(String info) {
        sendMsg(new DebugInfo(DebugType.Info, info));
    }

    public void sendFail(String errorInfo) {
        sendMsg(new DebugInfo(DebugType.Fail, errorInfo));
    }

    public void sendSuccess(String successInfo) {
        sendMsg(new DebugInfo(DebugType.Success, successInfo));
    }
}