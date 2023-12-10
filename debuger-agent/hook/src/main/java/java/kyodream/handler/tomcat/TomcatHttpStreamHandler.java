package java.kyodream.handler.tomcat;


import java.kyodream.RecordManager;
import java.kyodream.record.HttpRequestRecord;
import java.kyodream.utils.Reflection;
import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * 记录每次请求的
 */
public class TomcatHttpStreamHandler {

    public static void atEnter(Object thisObject, Object[] requestResponse) {
        if (RecordManager.requestThreadLocal.get() == null && requestResponse[0] != null) {
            if (isForward(requestResponse[0])) {
                return;
            }
            String servletName = getServletName(thisObject);
            LinkedList<String> filterList = getFilterList(thisObject);
            HttpRequestRecord requestRecord = getRequestRecord(requestResponse[0], servletName, filterList);
            RecordManager.requestThreadLocal.set(requestRecord);
        }

    }

    private static boolean isForward(Object request) {
        if (!request.getClass().getName().equals("org.apache.catalina.core.ApplicationHttpRequest")) {
            return false;
        }
        String dispatcherType = Reflection.getFieldObjectInParentByNameOneByOne(request, "dispatcherType", "name").toString();
        if (dispatcherType.equals("FORWARD")) {
            return true;
        }
        return false;
    }

    private static String getServletName(Object thisObject) {
        Object servlet = Reflection.getFieldObjectInParentByName(thisObject, "servlet");
        return servlet.getClass().getName();
    }

    private static LinkedList<String> getFilterList(Object thisObject) {
        LinkedList<String> result = new LinkedList<>();
        Object filters = Reflection.getFieldObjectInParentByName(thisObject, "filters");
        for (int i = 0; i < Array.getLength(filters); i++) {
            Object filterObject = Array.get(filters, i);
            if (filterObject == null) {
                continue;
            }
            result.add(filterObject.getClass().getName());
        }
        return result;
    }

    private static HttpRequestRecord getRequestRecord(Object requestObject, String servletName, LinkedList<String> filterList) {
        String getRequestURL = Reflection.invokeMethod(requestObject, "getRequestURL", null, null).toString();
        String methodName = Reflection.invokeMethod(requestObject, "getMethod", null, null).toString();
        return new HttpRequestRecord(getRequestURL, methodName, filterList, servletName);
    }

    public static void atExit() {
        RecordManager.requestThreadLocal.set(null);
    }
}
