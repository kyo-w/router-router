package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jetty extends MiddleBase {
    public Jetty() {
        init();
    }

    @Override
    String getTitle() {
        return "Jetty";
    }

    @Override
    void init() {
        getClassList().add("org.eclipse.jetty.servlet.ServletHandler");
        getRouterMethod().add("getMatchedServlet");
    }

    @Override
    public void handleEvent(MethodEntryEvent event) {
        StackFrame frame = null;
        try {
            frame = event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        if (frame == null) {
            return;
        }

        Field servletPathMap = frame.thisObject().referenceType().fieldByName("_servletPathMap");
        ObjectReference servletPathMapObject = (ObjectReference) frame.thisObject().getValue(servletPathMap);
        Field mappings = servletPathMapObject.referenceType().fieldByName("_mappings");
        ObjectReference value = (ObjectReference) servletPathMapObject.getValue(mappings);
        Method toString = value.referenceType().methodsByName("toString").get(0);
        String urlRaw = null;
        try {
            Value value1 = value.invokeMethod(event.thread(), toString, Collections.emptyList(), 0);
            urlRaw = value1.toString();
        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        } catch (InvocationException e) {
            e.printStackTrace();
        }

        if (urlRaw != null) {
            convertMap(urlRaw);
        }
    }

    public void convertMap(String urlRaw) {
        Pattern compile = Pattern.compile("MappedResource\\[.*?\\{(.*?)\\}.*?==(.*?)\\@.*?\\]");
        Matcher matcher = compile.matcher(urlRaw);
        while (matcher.find()) {
            String url = matcher.group(1);
            String className = matcher.group(2);

            if (url.startsWith("*")) {
                getExtensionMap().put(url, className);
                if (Connector.record.containsKey(className)) {
                    Connector.record.get(className).setType(Type.Extension);
                    Connector.record.get(className).setData(url.substring(url.length() - 1));
                }
            } else if (url.contains("*")) {
                if (Connector.record.containsKey(className)) {
                    Connector.record.get(className).setType(Type.Wild);
                    Connector.record.get(className).setData(url.substring(url.length() - 1));
                }
                getWildMap().put(url, className);
            } else {
                getExactMap().put(url, className);
            }
        }
    }
}
