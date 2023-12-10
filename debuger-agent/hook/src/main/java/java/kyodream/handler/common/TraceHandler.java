package java.kyodream.handler.common;



import java.kyodream.RecordManager;
import java.kyodream.record.HttpRequestRecord;
import java.kyodream.record.StackTrace;
import java.util.Arrays;

/**
 * 填充堆栈消息
 */
public class TraceHandler {
    public static void atEnter() {
        HttpRequestRecord httpRequest = RecordManager.requestThreadLocal.get();
        if (httpRequest != null) {
            StackTraceElement[] threadTrace = Thread.currentThread().getStackTrace();
            threadTrace = Arrays.copyOfRange(threadTrace, 2, threadTrace.length - 2);
            StackTrace[] stackTraces = new StackTrace[threadTrace.length];
            for (int i = 0; i < stackTraces.length; i++) {
                stackTraces[i] = new StackTrace(threadTrace[i].getClassName(), threadTrace[i].getMethodName());
            }
            httpRequest.setStackTraces(stackTraces);
            RecordManager.recordRequest(httpRequest);
            RecordManager.requestThreadLocal.remove();
        }
    }
}