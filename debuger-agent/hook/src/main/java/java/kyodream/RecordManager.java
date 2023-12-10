package java.kyodream;

import java.kyodream.record.Context;
import java.kyodream.record.Framework;
import java.kyodream.record.HttpRequestRecord;
import java.util.HashMap;

public class RecordManager {

    public final static String RequestKey = "request";
    public final static String FrameworkKey = "framework";
    public final static String ContextKey = "context";
    private static HashMap<String, Record> recordHashMap = new HashMap<>();

    public static void registryHook(String recordKey, Record hook) {
        recordHashMap.put(recordKey, hook);
    }

    //HOOK线程上下文
    public static ThreadLocal<HttpRequestRecord> requestThreadLocal = new ThreadLocal<>();

    public static void recordRequest(HttpRequestRecord httpRequest) {
        ((RecordRequestHook) recordHashMap.get(RequestKey)).recordRequest(httpRequest);
    }

    public static void recordFramework(Framework framework) {
        ((RecordFrameworkHook) recordHashMap.get(FrameworkKey)).recordFramework(framework);
    }

    public static void recordContext(Context context) {
        ((RecordContextHook) recordHashMap.get(ContextKey)).recordContext(context);
    }


    public static class Record {
    }

    public abstract static class RecordRequestHook extends Record {
        public abstract void recordRequest(HttpRequestRecord httpRequest);
    }

    public abstract static class RecordFrameworkHook extends Record {
        public abstract void recordFramework(Framework framework);
    }

    public abstract static class RecordContextHook extends Record {
        public abstract void recordContext(Context context);
    }
}
