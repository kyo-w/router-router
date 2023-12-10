package java.kyodream.handler.jetty;


import java.kyodream.RecordManager;
import java.kyodream.record.HttpRequestRecord;
import java.kyodream.utils.Reflection;
import java.util.LinkedList;

public class JettyHttpStreamHandler {
    public static void AtEnter(Object servletHolderObject, Object[] args) {
        HttpRequestRecord httpRequestRecord = RecordManager.requestThreadLocal.get();
        //一次请求只进行一次流处理
        if (httpRequestRecord == null) {
            String httpUrl = getHttpUrl(args[0]);
            String httpMethod = getHttpMethod(args[0]);
            String handlerClassName = getHandlerClassName(servletHolderObject);
            LinkedList<String> filterMap = JettyFilterStreamHandler.filterMap.get();
            JettyFilterStreamHandler.filterMap.set(null);
            RecordManager.requestThreadLocal.set(new HttpRequestRecord(httpUrl, httpMethod, filterMap, handlerClassName));
        }
    }

    public static void AtExit() {
        RecordManager.requestThreadLocal.set(null);
        JettyFilterStreamHandler.filterMap.set(null);
    }


    private static String getHandlerClassName(Object servletHolderObject) {
        return (String) Reflection.getFieldObjectInParentByName(servletHolderObject, "_className");
    }

    private static String getHttpMethod(Object requestObject) {
        return Reflection.invokeMethod(requestObject, "getMethod", null, null).toString();
    }

    private static String getHttpUrl(Object requestObject) {
        return Reflection.invokeMethod(requestObject, "getRequestURL", null, null).toString();
    }
}
