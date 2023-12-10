package kyodream.event;

import org.springframework.web.socket.WebSocketSession;

public class RequestSubscribe extends SubscribeImpl {
    public RequestSubscribe(WebSocketSession session) {
        super(session);
    }

    @Override
    public Event getEventType() {
        return Event.DUMP_EVENT;
    }
}
