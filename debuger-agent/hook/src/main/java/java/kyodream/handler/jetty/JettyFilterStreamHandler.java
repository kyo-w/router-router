package java.kyodream.handler.jetty;


import java.kyodream.utils.Reflection;
import java.util.LinkedList;

public class JettyFilterStreamHandler {
    public static ThreadLocal<LinkedList<String>> filterMap = new ThreadLocal<>();

    public static void AtExit(Object returnObject) {
        filterMap.set(getFilterMap(returnObject));
    }

    private static LinkedList<String> getFilterMap(Object chainRoot) {
        LinkedList<String> result = new LinkedList<>();
        Object filterNode = chainRoot;
        while (true) {
            Object filterHolder = null;
            try {
//                链表检测到异常说明已经到遍历完了
                filterHolder = Reflection.getFieldObjectInParentByName(filterNode, "_filterHolder");
                if (filterHolder == null) {
                    break;
                }
            } catch (Exception e) {
                break;
            }
            Object filter = Reflection.getFieldObjectInParentByName(filterHolder, "_filter");
            result.add(filter.getClass().getName());
            if (!JettyRegistryHandler.isJetty8()) {
                try {
                    filterNode = Reflection.getFieldObjectInParentByName(filterNode, "_filterChain");
                } catch (Exception e) {
//                    尝试解析Jetty8格式
                    filterNode = Reflection.getFieldObjectInParentByName(filterNode, "_next");
                }
            } else {
                filterNode = Reflection.getFieldObjectInParentByName(filterNode, "_next");
            }
        }
        return result;
    }

}
