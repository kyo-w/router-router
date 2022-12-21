package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.tools.corba.se.idl.MethodEntry;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Struts implements Handle {
    private static List<String> className = new ArrayList<>();
    private static Set<String> routerMethod = new HashSet<>();
    private static HashMap<String, String> map = new HashMap<>();

    static {
        className.add("org.apache.struts.config.impl.ModuleConfigImpl");
        routerMethod.add("findMessageResourcesConfigs");
    }

    @Override
    public void getRouter(MethodEntryEvent event) {
        routerMethod.forEach(elem -> {
            if (event.method().name().equals(elem)) {
                handleEvent(event);
            }
        });
    }

    public void handleEvent(MethodEntryEvent event) {
        ObjectReference root = null;
        try {
            root = event.thread().frame(0).thisObject();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }

        ObjectReference map = null;
        String rawData = null;
        if (root != null) {
            Field actionConfigs = root.referenceType().fieldByName("actionConfigs");
            map = (ObjectReference) root.getValue(actionConfigs);
        }
        if (map != null) {
            Method toString = map.referenceType().methodsByName("toString").get(0);
            try {
                rawData = map.invokeMethod(event.thread(), toString, Collections.EMPTY_LIST, 0).toString();
            } catch (InvalidTypeException e) {
                e.printStackTrace();
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            } catch (InvocationException e) {
                e.printStackTrace();
            }
        }
        if (rawData != null) {
            handleRawDate(rawData);
        }
    }

    public void handleRawDate(String rawData) {
        Pattern compile = Pattern.compile("path=(.*?),validate.*?type=(.*?)[,}]");
        Matcher matcher = compile.matcher(rawData);
        while (matcher.find()) {
            putMap(matcher.group(1), matcher.group(2));
        }
    }

    private void putMap(String url, String className) {
        RecordData recordData = Connector.record.get("org.apache.struts.action.ActionServlet");
        if (recordData.getType().equals(Type.Wild)) {
            String prefix = recordData.getData();
            if (prefix == "") {
                map.put(url, className);
                return;
            }

            if (prefix.endsWith("/")) {
                map.put(prefix + url.substring(1), className);
                return;
            } else {
                map.put(prefix + url, className);
            }
        } else {
            map.put(url, className);
        }
    }


    @Override
    public void clearRouter() {
        map = null;
        map = new HashMap<>();
    }

    @Override
    public String[][] wrapperRouter() {
        String[][] result = new String[map.size()][];
        int point = 0;
        for (Map.Entry<String, String> elem : map.entrySet()) {
            result[point++] = new String[]{"Struts", elem.getKey(), elem.getValue()};
        }
        return result;
    }

    @Override
    public List<String> getHookClassName() {
        return this.className;
    }
}
