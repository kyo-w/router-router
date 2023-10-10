package com.kyodream.debugger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyodream.debugger.pojo.ApiStack;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/stack")
public class StackWebSocket {
    private static CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        this.SESSIONS.add(session);
    }

    public void sendStack(ApiStack apiStack) {
        String stackInfo = null;
        try {
            stackInfo = objectMapper.writeValueAsString(apiStack);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (stackInfo != null) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    synchronized (session) {
                        try {
                        session.getBasicRemote().sendText(stackInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

}
