package kyodream.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public abstract class SubscribeImpl implements ISubscriber {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketSession session;

    public SubscribeImpl(WebSocketSession session) {
        this.session = session;
        registry();
    }

    @Override
    public void receiveMessage(Object message) throws Exception {
        String info = objectMapper.writeValueAsString(message);
        TextMessage textMess = new TextMessage(info);
        session.sendMessage(textMess);
    }
}
