package kyodream.event;

public interface IPublisher {
    Event getEventType();

    default void publishMessage(Object message) {
        if (getEventType() == null) {
            throw new IllegalArgumentException("消息发送失败!消息类型为null.");
        }
        EventManager.getEventManager().publishMessage(getEventType(), message);
    }
}
