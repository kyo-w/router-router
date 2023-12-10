package kyodream.event;

import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.util.HashSet;
import java.util.Set;

public class FrameworkEvent extends RecordManager.RecordFrameworkHook implements IPublisher{

    public static Set<Framework> frameworkSet = new HashSet<>();

    @Override
    public Event getEventType() {
        return Event.FRAMEWORK_EVENT;
    }

    @Override
    public void recordFramework(Framework framework) {
        frameworkSet.add(framework);
        publishMessage(framework);
    }
}
