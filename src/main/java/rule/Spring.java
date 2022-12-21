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
//        spring-webmvc:5.2.x
        routerMethod.add("getMappings");
//        spring-webmvc:5.3.x
        routerMethod.add("getMappingsByDirectPath");
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
        String methodName = event.method().name();
//        spring-webmvc:5.2.x
        if (methodName.equals("getMappings")) {
            Field mappingLookupField = null;
            List<Method> toString = null;
            Value stringValue = null;
            ObjectReference objectReference = null;
            try {
                mappingLookupField = event.thread().frame(0).thisObject().referenceType().fieldByName("mappingLookup");
                objectReference = (ObjectReference) event.thread().frame(0).thisObject().getValue(mappingLookupField);
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            }
            toString = objectReference.referenceType().methodsByName("toString", "()Ljava/lang/String;");
            try {
                stringValue = objectReference.invokeMethod(((MethodEntryEvent) event).thread(), toString.get(0), Collections.emptyList(), 0);
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
        } else if (methodName.equals("getMappingsByDirectPath")) {
//            spring-webmvc:5.3.x
            Field registry = null;
            ObjectReference registryTable = null;
            ObjectReference pathLookupObject = null;
            try {
                registry = event.thread().frame(0).thisObject().referenceType().fieldByName("registry");
                Field pathLookup = event.thread().frame(0).thisObject().referenceType().fieldByName("pathLookup");
                registryTable = (ObjectReference) event.thread().frame(0).thisObject().getValue(registry);
                pathLookupObject = (ObjectReference) event.thread().frame(0).thisObject().getValue(pathLookup);
                handleSpring53(event, pathLookupObject, registryTable);

            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            } catch (InvocationException e) {
                e.printStackTrace();
            } catch (InvalidTypeException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSpring53(MethodEntryEvent event, ObjectReference pathObject, ObjectReference registryTable) throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
        List<Method> values = registryTable.referenceType().methodsByName("values");
        ArrayReference arrayReference = null;
        ObjectReference value = (ObjectReference) registryTable.invokeMethod(event.thread(), values.get(0), Collections.EMPTY_LIST, 0);
        List<Method> toArray = value.referenceType().methodsByName("toArray");
        arrayReference = (ArrayReference) value.invokeMethod(event.thread(), toArray.get(0), Collections.EMPTY_LIST, 0);
        for (Value objectReference : arrayReference.getValues()) {
            Field mapping = ((ObjectReference) objectReference).referenceType().fieldByName("mapping");
            ObjectReference mappingObject = (ObjectReference) ((ObjectReference) objectReference).getValue(mapping);
            Field pathPatternsCondition = mappingObject.referenceType().fieldByName("pathPatternsCondition");
            ObjectReference pathPatternsConditionObject = (ObjectReference) mappingObject.getValue(pathPatternsCondition);
            Field patterns = pathPatternsConditionObject.referenceType().fieldByName("patterns");
            ObjectReference patternsObject = (ObjectReference) pathPatternsConditionObject.getValue(patterns);
            List<Method> first = patternsObject.referenceType().methodsByName("first");
            ObjectReference patternObject = (ObjectReference) patternsObject.invokeMethod(event.thread(), first.get(0), Collections.EMPTY_LIST, 0);
            Field patternString = patternObject.referenceType().fieldByName("patternString");
            String url = patternObject.getValue(patternString).toString().replace("\"", "");

            Field handlerMethod = ((ObjectReference) objectReference).referenceType().fieldByName("handlerMethod");
            ObjectReference handlerMethodObject = (ObjectReference) ((ObjectReference) objectReference).getValue(handlerMethod);
            Field description = handlerMethodObject.referenceType().fieldByName("description");
            String className = handlerMethodObject.getValue(description).toString().replace("\"", "");
            putMap(url, className);
        }
    }


    public void convertMap(String value) {
        // 格式化toString
        //处理为以下格式
        //   {api} = {className},
        String text = value.substring(2).substring(0, value.length() - 2) + ", ";
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
        } else {
//            以springboot的形式存在或者直接HOOk/*
            map.put(url, className);
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
