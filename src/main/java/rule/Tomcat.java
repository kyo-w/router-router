package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;

public class Tomcat extends MiddleBase {

    public Tomcat() {
        init();
    }

    @Override
    String getTitle() {
        return "Tomcat";
    }

    @Override
    void init() {
        getHookClassName().add("org.apache.catalina.mapper.Mapper");
        getHookClassName().add("org.apache.tomcat.util.http.mapper.Mapper");
        getRouterMethod().add("internalMapExactWrapper");
        getRouterMethod().add("internalMapWildcardWrapper");
        getRouterMethod().add("internalMapExtensionWrapper");
    }
    
    @Override
    void handleEvent(MethodEntryEvent event) {
    }

    public void getRouter(MethodEntryEvent event) {
        if (event.method().name().equals("internalMapExactWrapper")) {
            handleEvent(event, getExactMap());
        } else if (event.method().name().equals("internalMapWildcardWrapper")) {
            handleEvent(event, getWildMap());
            connectionWild();
        } else if (event.method().name().equals("internalMapExtensionWrapper")) {
            handleEvent(event, getExtensionMap());
            connectionExtension();
        }
    }

    /**
     * 从本地方法栈中获取,internalMapExactWrapper/internalMapWildcardWrapper/internalMapExtensionWrapper
     * 这些方法的第一参数都为路由映射表的堆栈信息，从此获取路由表
     *
     * @param event
     * @param map
     */
    private void handleEvent(MethodEntryEvent event, HashMap<String, String> map) {
        StackFrame frame = null;
        List<LocalVariable> arguments = null;
        ArrayReference root = null;
        try {
            frame = event.thread().frame(0);
            arguments = event.method().arguments();
        } catch (IncompatibleThreadStateException | AbsentInformationException e) {
            e.printStackTrace();
        }
        if (frame != null && arguments != null) {
            root = (ArrayReference) frame.getValue(arguments.get(0));
        }

        if (root != null) {
            root.getValues().forEach(elem -> {
                Field name = ((ObjectReference) elem).referenceType().fieldByName("name");
                Value url = ((ObjectReference) elem).getValue(name);

                Field object = ((ObjectReference) elem).referenceType().fieldByName("object");
                Value value2 = ((ObjectReference) elem).getValue(object);
                Field servletClass = ((ObjectReference) value2).referenceType().fieldByName("servletClass");
                Value className = ((ObjectReference) value2).getValue(servletClass);
                map.put(url.toString().replace("\"", ""), className.toString().replace("\"", ""));
            });
        }
    }
}
