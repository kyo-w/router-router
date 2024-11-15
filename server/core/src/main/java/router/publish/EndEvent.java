package router.publish;

public class EndEvent implements Event {
    private EventType eventType;
    private Long uniqId;
    private String message;

    public EndEvent(EventType eventType, Long uniqId, String message) {
        this.eventType = eventType;
        this.message = message;
        this.uniqId = uniqId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUniqId() {
        return uniqId;
    }

    public void setUniqId(Long uniqId) {
        this.uniqId = uniqId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public EventType getType() {
        return eventType;
    }
}
