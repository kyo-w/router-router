package kyodream.event;

import org.springframework.web.socket.WebSocketSession;

public class FrameworkSubscribe extends SubscribeImpl {
    public FrameworkSubscribe(WebSocketSession session) {
        super(session);
    }

    @Override
    public Event getEventType() {
        return Event.FRAMEWORK_EVENT;
    }
}
