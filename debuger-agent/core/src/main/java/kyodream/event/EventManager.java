package kyodream.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class EventManager {
    private Map<Event, List<ISubscriber>> subscribeMap = new HashMap<>();
    private int processors = Runtime.getRuntime().availableProcessors();
    private ExecutorService threadPool = new ThreadPoolExecutor(processors, processors * 5, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(200), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static EventManager eventManager = new EventManager();

    private EventManager() {
    }

    public void subscribe(ISubscriber subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("当前对象不存在");
        }
        //获取消息类型
        Event eventType = subscriber.getEventType();
        if (eventType == null) {
            throw new IllegalArgumentException("event type null");
        }
        //绑定订阅对象
        if (subscribeMap.get(eventType) == null) {
            List<ISubscriber> subscribes = new ArrayList<>();
            subscribes.add(subscriber);
            subscribeMap.put(eventType, subscribes);
        } else {
            subscribeMap.get(eventType).add(subscriber);
        }
    }

    public void unSubscribe(ISubscriber subscriber) {
        if (subscriber != null && subscriber.getEventType() != null) {
            List<ISubscriber> subscribers = subscribeMap.get(subscriber.getEventType());
            if (subscribers != null) {
                subscribers.remove(subscriber);
            }
        }
    }

    public void publishMessage(Event messageType, Object message) {
        List<ISubscriber> iSubscribers = subscribeMap.get(messageType);
        if (iSubscribers != null && !iSubscribers.isEmpty()) {
            iSubscribers.forEach(subscriber -> {
                this.threadPool.submit(() -> {
                    try {
                        subscriber.receiveMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    public static EventManager getEventManager() {
        return eventManager;
    }
}
