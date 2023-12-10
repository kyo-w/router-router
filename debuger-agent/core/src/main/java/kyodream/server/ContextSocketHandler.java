package kyodream.server;

import kyodream.RecordManagerImpl;
import kyodream.event.ContextSubscribe;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ContextSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ContextSubscribe contextSubscribe = new ContextSubscribe(session);
        String id = session.getId();
        RecordManagerImpl.registrySubscribe(id, contextSubscribe);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String id = session.getId();
        RecordManagerImpl.removeSubscribe(id);
    }
}
