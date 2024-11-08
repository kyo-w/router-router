package router.publish;

import router.mapping.MiddlewareMapping;
import router.mapping.FrameworkMapping;

public class StdOutPublish implements IPublish {
    public void Event(EventType eventType, EventPackage eventPackage) {
        System.out.println("Router Event[ " +eventType.toString() + " ]: "  + eventPackage.getMsg());
    }

    public void Error(Exception msg) {
        System.out.println("Router Error Event: " + msg.toString());
    }
}
