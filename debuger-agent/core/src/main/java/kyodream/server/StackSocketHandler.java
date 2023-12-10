package kyodream.server;

import kyodream.RecordManagerImpl;
import kyodream.event.RequestSubscribe;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class StackSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        RequestSubscribe requestSubscribe = new RequestSubscribe(session);
        String id = session.getId();
        RecordManagerImpl.registrySubscribe(id, requestSubscribe);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String id = session.getId();
        RecordManagerImpl.removeSubscribe(id);
    }
}
