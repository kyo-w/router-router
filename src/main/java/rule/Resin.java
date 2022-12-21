package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Resin extends MiddleBase {

    public Resin(){
        init();
    }

    @Override
    String getTitle() {
        return "Resin";
    }

    @Override
    void init() {
        getClassList().add("com.caucho.server.webapp.AccessLogFilterChain");
        getRouterMethod().add("doFilter");
    }

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

        Field webApp = frame.thisObject().referenceType().fieldByName("_webApp");
        ObjectReference value = (ObjectReference) frame.thisObject().getValue(webApp);
        Field servletMapper = value.referenceType().fieldByName("_servletMapper");
        ObjectReference servletMapperObject = (ObjectReference) value.getValue(servletMapper);
        Field servletNamesMap = servletMapperObject.referenceType().fieldByName("_servletNamesMap");
        ObjectReference servletNamesMapObject = (ObjectReference) servletMapperObject.getValue(servletNamesMap);
        Method toString = servletNamesMapObject.referenceType().methodsByName("toString").get(0);
        String patternRwa = null;
        try {
            Value value1 = servletNamesMapObject.invokeMethod(event.thread(), toString, Collections.emptyList(), 0);
            patternRwa = value1.toString();
        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        } catch (InvocationException e) {
            e.printStackTrace();
        }

        if (patternRwa != null) {
            handlePatternRaw(patternRwa);
        }

        Field servletManager = servletMapperObject.referenceType().fieldByName("_servletManager");
        ObjectReference servletManager1 = (ObjectReference) servletMapperObject.getValue(servletManager);
        Field servlets = servletManager1.referenceType().fieldByName("_servlets");
        ObjectReference servletsObject = (ObjectReference) servletManager1.getValue(servlets);
        Method toString1 = servletsObject.referenceType().methodsByName("toString").get(0);
        String urlRaw = null;
        try {
            Value value1 = servletsObject.invokeMethod(event.thread(), toString1, Collections.emptyList(), 0);
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
            handleUrlRaw(urlRaw);
        }
    }

    private void handleUrlRaw(String urlRaw) {
        Pattern compile = Pattern.compile("\\[name=(.*?),class=class (.*?)\\]");
        Matcher matcher = compile.matcher(urlRaw);
        while (matcher.find()) {
            String alias = matcher.group(1);
            String className = matcher.group(2);
            for (Map.Entry<String, String> elem : getExactMap().entrySet()) {
                if (alias.equals(elem.getValue())) {
                    getExactMap().replace(elem.getKey(), className);
                }
            }
            for (Map.Entry<String, String> elem : getWildMap().entrySet()) {
                if (alias.equals(elem.getValue())) {
                    getWildMap().replace(elem.getKey(), className);
                    if (Connector.record.containsKey(className)) {
                        Connector.record.get(className).setType(Type.Wild);
                        Connector.record.get(className).setData(elem.getKey());
                    }
                }
            }
            for (Map.Entry<String, String> elem : getExtensionMap().entrySet()) {
                if (alias.equals(elem.getValue())) {
                    getExtensionMap().replace(elem.getKey(), className);
                    if (Connector.record.containsKey(className)) {
                        Connector.record.get(className).setType(Type.Extension);
                        Connector.record.get(className).setData(elem.getKey());
                    }
                }
            }
        }
    }

    private void handlePatternRaw(String patternRwa) {
        Pattern compile = Pattern.compile("\\[url-pattern=(.*?), +name=(.*?)\\]");
        Matcher matcher = compile.matcher(patternRwa);
        while (matcher.find()) {
            String alias = matcher.group(2);

            String replace = matcher.group(1).replace("url-pattern=", "");
            String[] split = replace.split(",");
            for (String elem : split) {
                if (elem.trim().startsWith("*")) {
                    getExtensionMap().put(elem, alias);
                } else if (elem.trim().contains("*")) {
                    getWildMap().put(elem, alias);
                    connectionWild();
                } else {
                    getExactMap().put(elem.trim(), alias);
                }
            }
        }
    }
}