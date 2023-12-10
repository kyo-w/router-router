package kyodream.event;

import java.kyodream.RecordManager;
import java.kyodream.record.Context;
import java.util.HashSet;
import java.util.Set;

public class ContextEvent extends RecordManager.RecordContextHook implements IPublisher {
    public static Set<Context> contextSet = new HashSet<>();

    @Override
    public Event getEventType() {
        return Event.CONTEXT_EVENT;
    }

    @Override
    public void recordContext(Context context) {
        contextSet.add(context);
        publishMessage(context);
    }
}
