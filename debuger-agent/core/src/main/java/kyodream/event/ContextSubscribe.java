package kyodream.event;

import org.springframework.web.socket.WebSocketSession;

public class ContextSubscribe extends SubscribeImpl {

    public ContextSubscribe(WebSocketSession session) {
        super(session);
    }

    @Override
    public Event getEventType() {
        return Event.CONTEXT_EVENT;
    }
}
