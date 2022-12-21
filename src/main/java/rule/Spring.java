package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spring implements Handle {
    private static List<String> className = new ArrayList<>();

    private static Set<String> routerMethod = new HashSet<>();

    private static HashMap<String, String> map = new HashMap<>();

    static {
        className.add("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry");
//        唯一路由
        routerMethod.add("getMappings");
    }

    @Override
    public void getRouter(MethodEntryEvent event) {
        routerMethod.forEach(elem -> {
            if (event.method().name().equals(elem)) {
                handleEvent(event, map);
            }
        });
    }

    @Override
    public void clearRouter() {
        map = null;
        map = new HashMap<>();
    }

    /**
     * 每次查询都会经过AbstractHandlerMethodMapping$MappingRegistry.getMappings
     * MappingRegistry中的mappingLookup即为路由表
     *
     * @param event
     * @param map
     */
    private void handleEvent(MethodEntryEvent event, HashMap<String, String> map) {
        Field mappingLookupField = null;
        ObjectReference mappingLookup = null;
        Value stringValue = null;

//        获取Spring MappingRegistry
        try {
            mappingLookupField = event.thread().frame(0).thisObject().referenceType().fieldByName("mappingLookup");
            mappingLookup = (ObjectReference) event.thread().frame(0).thisObject().getValue(mappingLookupField);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }

        List<Method> toString = mappingLookup.referenceType().methodsByName("toString", "()Ljava/lang/String;");
        try {
            stringValue = mappingLookup.invokeMethod(((MethodEntryEvent) event).thread(), toString.get(0), Collections.emptyList(), 0);
        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        } catch (InvocationException e) {
            e.printStackTrace();
        }
        convertMap(stringValue.toString());

    }


    public void convertMap(String value) {
        // 格式化toString
        //处理为以下格式
        //   {api} = {className},
        String text = value.substring(2).substring(0, value.length() - 2) + ", ";
        System.out.println(text);
        Pattern compile = Pattern.compile("\\{(.*?)\\}=(.*?), ");
        Matcher matcher = compile.matcher(text);
        while (matcher.find()) {
            putMap(matcher.group(1), matcher.group(2));
        }
    }

    // 存放路由信息时，必须经过这个方法存放
    private void putMap(String url, String className) {
        RecordData recordData = Connector.record.get("org.springframework.web.servlet.DispatcherServlet");
//        存在说明，spring是以容器的形式
        if (recordData.getType().equals(Type.Wild)) {
            String prefix = recordData.getData();
            if (prefix == null) {
                map.put(url, className);
                return;
            }

            if (prefix.endsWith("/")) {
                map.put(prefix + url.substring(1), className);
                return;
            } else {
                map.put(prefix + url, className);
            }
        }else{
//            以springboot的形式存在或者直接HOOk/*
            map.put(url,className);
        }
    }

    @Override
    public String[][] wrapperRouter() {
        String[][] result = new String[map.size()][];
        int point = 0;
        for (Map.Entry<String, String> elem : map.entrySet()) {
            result[point++] = new String[]{"Spring", elem.getKey(), elem.getValue()};
        }
        return result;
    }

    @Override
    public List<String> getHookClassName() {
        return this.className;
    }
}
