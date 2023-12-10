package com.kyodream.debugger.service;

import kyodream.map.AnalystsType;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws/discovery")
public class DiscoverWebSocket extends AbstractWS {
    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    public void discoveryMiddleWare(AnalystsType type) {
        super.sendObject(new TargetInfo(type.name(), "middle"));
    }

    public void discoveryFramework(AnalystsType type) {
        super.sendObject(new TargetInfo(type.name(), "framework"));
    }

    @OnClose
    public void close(Session session) {
        super.close(session);
    }

    public static class TargetInfo {
        private String name;
        private String type;

        public TargetInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
