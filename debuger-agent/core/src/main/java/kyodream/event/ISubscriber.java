package kyodream.event;

public interface ISubscriber {
    Event getEventType();

    default void registry() {
        EventManager.getEventManager().subscribe(this);
    }

    default void unSubscribe() {
        EventManager.getEventManager().unSubscribe(this);
    }

    void receiveMessage(Object message) throws Exception;

}
