package kyodream;

import kyodream.event.ContextEvent;
import kyodream.event.FrameworkEvent;
import kyodream.event.RequestEvent;
import kyodream.event.SubscribeImpl;

import java.kyodream.RecordManager;
import java.util.HashMap;

public class RecordManagerImpl {
    private ContextEvent contextEvent;
    private FrameworkEvent frameworkEvent;
    private RequestEvent requestEvent;
    private static HashMap<String, SubscribeImpl> subscribeHashMap = new HashMap<>();

    public RecordManagerImpl() {
        contextEvent = new ContextEvent();
        frameworkEvent = new FrameworkEvent();
        requestEvent = new RequestEvent();
    }

    public void registryHook() {
        RecordManager.registryHook(RecordManager.ContextKey, contextEvent);
        RecordManager.registryHook(RecordManager.FrameworkKey, frameworkEvent);
        RecordManager.registryHook(RecordManager.RequestKey, requestEvent);
    }

    public static void registrySubscribe(String id, SubscribeImpl subscribe) {
        subscribeHashMap.put(id, subscribe);
    }

    public static void removeSubscribe(String id) {
        SubscribeImpl remove = subscribeHashMap.remove(id);
        if (remove != null) {
            remove.unSubscribe();
        }
    }
}
