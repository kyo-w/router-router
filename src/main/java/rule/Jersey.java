package rule;

import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;

public class Jersey implements Handle{
    private static List<String> className = new ArrayList<>();
    private static Set<String> routerMethod = new HashSet<>();

    private static HashMap<String, String> map = new HashMap<>();

    static {
        className.add("com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule");
//        唯一路由
        routerMethod.add("accept");
    }

    @Override
    public void getRouter(MethodEntryEvent event) {
        routerMethod.forEach(elem->{
            if(event.method().name().equals(elem)){
                handleEvent(event, map);
            }
        });
    }

    public void handleEvent(MethodEntryEvent event, HashMap<String, String> map){
        StackFrame frame = null;
        try {
            frame = event.thread().frame(0);
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        if(frame == null){
            return;
        }

        //获取ruleList对象
        Field rules = frame.thisObject().referenceType().fieldByName("rules");
        Value value = frame.thisObject().getValue(rules);
        Field rules1 = ((ObjectReference) value).referenceType().fieldByName("rules");
        ObjectReference ruleList = (ObjectReference) ((ObjectReference) value).getValue(rules1);

        //遍历ArrayList对象获取api资产
        Field size = ruleList.referenceType().fieldByName("size");
        int num = ((IntegerValue)ruleList.getValue(size)).value();
        Method get = ruleList.referenceType().methodsByName("get").get(0);
        for(int i=0; i < num; i++){
            IntegerValue integerValue = event.virtualMachine().mirrorOf(i);
            List<Value> arg = new ArrayList<>();
            arg.add(integerValue);
            ObjectReference single = null;
            try {
                single = (ObjectReference) ruleList.invokeMethod(((MethodEntryEvent) event).thread(), get, arg, 0);
            } catch (InvalidTypeException e) {
                e.printStackTrace();
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            } catch (InvocationException e) {
                e.printStackTrace();
            }
            Field r = single.referenceType().fieldByName("r");
            ObjectReference rule = (ObjectReference) single.getValue(r);
            Field rule1 = rule.referenceType().fieldByName("rule");
            ObjectReference resourceClass = (ObjectReference) rule.getValue(rule1);
            if(resourceClass.type().name().equals("com.sun.jersey.server.impl.uri.rules.ResourceClassRule")){
                handleResourceClassRule(resourceClass);
            }else if(resourceClass.type().name().equals("com.sun.jersey.server.impl.uri.rules.ResourceObjectRule")){
                handleResourceObjectRule(resourceClass);
            }else if(resourceClass.type().name().equals("com.sun.jersey.server.impl.uri.rules.SubLocatorRule")){
                handleSubLocatorRule(resourceClass);
            }
        }
    }

    private void handleResourceClassRule(ObjectReference resourceClass) {
        Field template = resourceClass.referenceType().fieldByName("template");
        ObjectReference templateValue = (ObjectReference) resourceClass.getValue(template);
        Field pattern = templateValue.referenceType().fieldByName("pattern");
        ObjectReference patternValue = (ObjectReference) templateValue.getValue(pattern);
        Field regex = patternValue.referenceType().fieldByName("regex");
        String url = patternValue.getValue(regex).toString().replace("\"", "");

        Field resourceClass1 = resourceClass.referenceType().fieldByName("resourceClass");
        ObjectReference classes = (ObjectReference) resourceClass.getValue(resourceClass1);
        Field name = classes.referenceType().fieldByName("name");
        String className = classes.getValue(name).toString().replace("\"", "");
        putMap(url, className);
    }

    /**
     * 高级路由，留空，可自行实现
     * @param resourceClass
     */
    private void handleResourceObjectRule(ObjectReference resourceClass){}

    /**
     * 高级路由，留空，可自行实现
     * @param resourceClass
     */
    private void handleSubLocatorRule(ObjectReference resourceClass){}

    @Override
    public void clearRouter() {
        map = null;
        map = new HashMap<>();
    }

    @Override
    public String[][] wrapperRouter() {
        String[][] result = new String[map.size()][];
        int point =0;
        for(Map.Entry<String, String> elem: map.entrySet()){
            result[point++] = new String[]{"Jersey", elem.getKey(), elem.getValue()};
        }
        return result;
    }

    @Override
    public List<String> getHookClassName() {
        return this.className;
    }


    // 存放路由信息时，必须经过这个方法存放
    private void putMap(String url, String className){
        RecordData recordData = Connector.record.get("com.sun.jersey.spi.container.servlet.ServletContainer");
        if(recordData.getType().equals(Type.Wild)) {
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
            map.put(url, className);
        }
    }
}
