package kyodream.event;

import java.kyodream.RecordManager;
import java.kyodream.record.HttpRequestRecord;
import java.util.HashSet;

public class RequestEvent extends RecordManager.RecordRequestHook implements IPublisher {

    public static HashSet<HttpRequestRecord> httpRequestRecords = new HashSet<>();

    @Override
    public Event getEventType() {
        return Event.DUMP_EVENT;
    }

    @Override
    public void recordRequest(HttpRequestRecord httpRequest) {
        httpRequestRecords.add(httpRequest);
        publishMessage(httpRequest);
    }
}
