package router.publish;

public class StdOutPublish implements IPublish {
    public void Event(Event event) {
        System.out.println("Router Event[ " + event.toString() + " ]");
    }

    public void Error(Error msg) {
        System.out.println("Router Error Event: " + msg.toString());
    }
}
