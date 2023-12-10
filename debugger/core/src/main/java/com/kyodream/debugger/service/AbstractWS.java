package com.kyodream.debugger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AbstractWS {
    private static HashMap<Class, CopyOnWriteArraySet<Session>> sessionMap = new HashMap();
    protected ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static ObjectMapper mapper = new ObjectMapper();


    public void onOpen(Session session) {
        CopyOnWriteArraySet<Session> sessions = sessionMap.get(this.getClass());
        if (sessions == null) {
            sessions = new CopyOnWriteArraySet<>();
            sessionMap.put(this.getClass(), sessions);
        }
        sessions.add(session);
    }

    protected void sendMsg(AnalystsInfo debugInfo) {
        if (sessionMap.get(this.getClass()) != null) {
            executorService.execute(new WsServerTask(sessionMap.get(this.getClass()), debugInfo));
        }
    }

    protected void sendObject(Object msg) {
        if (sessionMap.get(this.getClass()) != null) {
            executorService.execute(() -> {
                String message = "";
                try {
                    message = mapper.writeValueAsString(msg);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                for (Session session : sessionMap.get(this.getClass())) {
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
            });
        }
    }

    public void close(Session session) {
        if (sessionMap.get(this.getClass()) != null) {
            sessionMap.get(this.getClass()).remove(session);
        }
    }

    public static class WsServerTask implements Runnable {
        private CopyOnWriteArraySet<Session> SESSIONS;
        private AnalystsInfo debugInfo;
        private static ObjectMapper mapper = new ObjectMapper();

        public WsServerTask(CopyOnWriteArraySet<Session> sessions, AnalystsInfo debugInfo) {
            SESSIONS = sessions;
            this.debugInfo = debugInfo;
        }

        @Override
        public void run() {
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
    }

    public static class AnalystsInfo {
        private AbstractWS.InfoType debugType;
        private Object debugMessage;

        private long date;

        public AnalystsInfo(AbstractWS.InfoType debugType, Object debugMessage) {
            this.debugType = debugType;
            this.debugMessage = debugMessage;
            this.date = Instant.now().toEpochMilli();
        }

        public AbstractWS.InfoType getDebugType() {
            return debugType;
        }

        public void setDebugType(AbstractWS.InfoType debugType) {
            this.debugType = debugType;
        }

        public Object getDebugMessage() {
            return debugMessage;
        }

        public void setDebugMessage(Object debugMessage) {
            this.debugMessage = debugMessage;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

    public enum InfoType {
        Success,
        Error,
        Warn
    }
}