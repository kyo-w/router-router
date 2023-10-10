package com.kyodream.debugger.service;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/middle/discovery")
public class DiscoverWebSocket {
    private static CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();


    private Set<String> map = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        this.SESSIONS.add(session);
    }

    public void discoveryMiddlewareOrFramework(String type) {
        if (!map.contains(type)) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    synchronized (session) {
                        try {
                            session.getBasicRemote().sendText(type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            map.add(type);
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

    public void cleanCache() {
        map.clear();
    }
}
