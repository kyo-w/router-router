package kyodream.server;

import kyodream.RecordManagerImpl;
import kyodream.event.FrameworkSubscribe;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class FrameworkSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        FrameworkSubscribe frameworkSubscribe = new FrameworkSubscribe(session);
        String id = session.getId();
        RecordManagerImpl.registrySubscribe(id, frameworkSubscribe);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String id = session.getId();
        RecordManagerImpl.removeSubscribe(id);
    }
}
