package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.SetAnalyse;
import com.kyodream.debugger.core.category.format.Format;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.VirtualMachine;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SpringMvc extends AbstractDataWrapper {
    private static Set<String> discoveryClass = new HashSet<>();

    static {
        /**
         * 抽象类：org.springframework.web.servlet.handler.AbstractUrlHandlerMapping
         */
        //    Implementation Controller interface,  use @Bean(name = "${/router}")
        discoveryClass.add("org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping");
        discoveryClass.add("org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping");
        discoveryClass.add("org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping");
        //    Implementation Controller interface, configuration the simpleUrlHandlerMapping
        discoveryClass.add("org.springframework.web.servlet.handler.SimpleUrlHandlerMapping");


        /**
         * 处理注解: @GetMapping/ @PostMapping ...
         */
        discoveryClass.add("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
    }

    private String prefix = null;
    private Set<ObjectReference> springObjects = new HashSet<>();
    private HashMap<String, String> abstractUrlHandlerMapping = new HashMap<>();

    private HashMap<String, String> requestMappingHandlerMapping = new HashMap<>();

    private static boolean modify = false;

    public void hasModify(){
        modify = true;
    }

    @Override
    public Set<String> getDiscoveryClass() {
        return discoveryClass;
    }

    public void registryPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        hasFind();
        objectReference.forEach(elem -> {
            String name = elem.referenceType().name();
            String[] parts = name.split("\\.");
            String lastPart = parts[parts.length - 1];
            debugWebSocket.sendInfo("发现Spring对象: " + lastPart);
        });
        this.springObjects.addAll(objectReference);
    }

    @Override
    public void analystsObject(VirtualMachine attach) {
        debugWebSocket.sendInfo("开始分析spring");
        for (ObjectReference springObject : this.springObjects) {
            String name = springObject.referenceType().name();
            if (name.equals("org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping") ||
                    name.equals("org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping") ||
                    name.equals("org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping") ||
                    name.equals("org.springframework.web.servlet.handler.SimpleUrlHandlerMapping")) {

                handlerAbstractUrlHandlerMapping(springObject);
            }
            if (name.equals("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")) {
                handlerRequestMappingHandlerMapping(springObject);
            }
        }
        debugWebSocket.sendInfo("分析spring结束");
    }

    private void handlerRequestMappingHandlerMapping(ObjectReference springObject) {
        boolean isLt4 = false;
        ObjectReference mappingRegistryObject = getFieldObject(springObject, "mappingRegistry");
        if (mappingRegistryObject == null) {
            debugWebSocket.sendInfo("目标框架属于低版本(version < 4.1.0.RELEASE), 修正字段");
            isLt4 = true;
        }
        if (!isLt4) {
            /**
             * 高版本spring中的RequestMappingHandlerMapping结构
             *  registry(k,v) -> RequestMappingInfo
             *      k(RequestMappingInfo)
             *          patterns(TreeSet)
             */
            ObjectReference registryList = getFieldObject(mappingRegistryObject, "registry");
            Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(registryList);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference requestMappingInfoObject = next.getValue();
                ObjectReference handlerMethodObject = getFieldObject(requestMappingInfoObject, "handlerMethod");
                ObjectReference method = getFieldObject(handlerMethodObject, "method");
                ObjectReference clazz = getFieldObject(method, "clazz");
                String className = ((StringReference) getFieldObject(clazz, "name")).value();
                ObjectReference mappingObject = getFieldObject(requestMappingInfoObject, "mapping");
                // 需要判断是否低于4.2.0
                ObjectReference patternsCondition = getFieldObject(mappingObject, "patternsCondition");
                if (patternsCondition != null) {
                    ObjectReference patterns = null;
                    if (patternsCondition != null) {
                        patterns = getFieldObject(patternsCondition, "patterns");
                        debugWebSocket.sendInfo("修正成功");
                        ArrayList<ObjectReference> treeSet = null;
                        try {
                            treeSet = SetAnalyse.getUnmodifiableSet(patterns);
                        } catch (Exception e) {
                        }
                        if (treeSet != null) {
                            Iterator<ObjectReference> iterator1 = treeSet.iterator();
                            while (iterator1.hasNext()) {
                                ObjectReference next1 = iterator1.next();
                                String url = ((StringReference) next1).value();
                                if(!modify) {
                                    String fullUrl = this.prefix.replace("*", "/" + url);
                                    requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                                }else{
                                    requestMappingHandlerMapping.put(url, className);
                                }
                            }
                        } else {
                            debugWebSocket.sendInfo("目标版本高于等于5.2.0.RELEASE, 路由存储方式改变修正");
                            ArrayList<ObjectReference> hashSetObjectRef = null;
                            try {
                                hashSetObjectRef = SetAnalyse.getHashSetObject(patterns);
                            } catch (Exception e) {
                            }
                            if (hashSetObjectRef != null) {
                                debugWebSocket.sendInfo("修正成功");
                                hashSetObjectRef.forEach(urlStringRef -> {
                                    if(!modify) {
                                        String fullUrl = this.prefix.replace("*", "/" + ((StringReference) urlStringRef).value());
                                        requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                                    }else{
                                        requestMappingHandlerMapping.put(((StringReference) urlStringRef).value(), className);
                                    }
                                });
                            } else {
                                debugWebSocket.sendFail("修正失败: RequestMappingHandlerMapping(注解路由)获取失败，路由缺失");
                            }
                        }
                    } else {
                        debugWebSocket.sendFail("修正失败: RequestMappingHandlerMapping(注解路由)获取失败，路由缺失");
                    }
                } else {
                    debugWebSocket.sendInfo("patternsCondition获取失败,确定版本 < 4.2.0.RELEASE, patternsCondition修正字段为pathPatternsCondition");
                    ObjectReference pathPatternsCondition = getFieldObject(mappingObject, "pathPatternsCondition");
                    ObjectReference patterns = getFieldObject(pathPatternsCondition, "patterns");
                    ArrayList<ObjectReference> treeSet = SetAnalyse.getTreeSet(patterns);
                    Iterator<ObjectReference> treeSetIterator = treeSet.iterator();
                    while (treeSetIterator.hasNext()) {
                        ObjectReference pathPattern = treeSetIterator.next();
                        StringReference patternString = (StringReference) getFieldObject(pathPattern, "patternString");
                        if(!modify) {
                            String fullUrl = this.prefix.replace("*", "/" + patternString.value());
                            requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                        }else{
                            requestMappingHandlerMapping.put(patternString.value(), className);
                        }
                    }
                }
            }
        } else {
            handleRequestMappingHandlerMappingLt4_0(springObject);
        }
    }

    private void handleRequestMappingHandlerMappingLt4_0(ObjectReference springObject) {
        ObjectReference handlerMethodsObjectRef = getFieldObject(springObject, "handlerMethods");
        if (handlerMethodsObjectRef == null) {
            debugWebSocket.sendFail("修正失败");
        } else {
            debugWebSocket.sendSuccess("修正成功");
            Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(handlerMethodsObjectRef);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference key = next.getKey();
                ObjectReference value = next.getValue();

                ObjectReference method = getFieldObject(value, "method");
                ObjectReference clazz = getFieldObject(method, "clazz");
                String className = ((StringReference) getFieldObject(clazz, "name")).value();

                ObjectReference patternsCondition = getFieldObject(key, "patternsCondition");
                ObjectReference patterns = getFieldObject(patternsCondition, "patterns");
                ArrayList<ObjectReference> unmodifiableSet = SetAnalyse.getUnmodifiableSet(patterns);
                Iterator<ObjectReference> iterator1 = unmodifiableSet.iterator();
                while (iterator1.hasNext()) {
                    String url = ((StringReference) iterator1.next()).value();
                    if(!modify) {
                        String fullUrl = this.prefix.replace("*", "/" + url);
                        requestMappingHandlerMapping.put(fullUrl, className);
                    }else{
                        requestMappingHandlerMapping.put(url, className);
                    }
                }
            }
        }
    }

    private void handlerAbstractUrlHandlerMapping(ObjectReference springObject) {
        ObjectReference handlerMapObject = getFieldObject(springObject, "handlerMap");
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            String url = ((StringReference) next.getKey()).value();
            ObjectReference value = next.getValue();
            String className = value.referenceType().name();
            if(!modify){
                String fullUrl = this.prefix.replace("*", "/" + url);
                abstractUrlHandlerMapping.put(Format.doubleSlash(fullUrl), className);
            }else{
                abstractUrlHandlerMapping.put(url, className);
            }
        }
    }


    @Override
    public void setHandleOrPlugin() {
        this.handleOrPlugin = "springMvc";
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> result = new HashMap<>();
        result.putAll(this.abstractUrlHandlerMapping);
        result.putAll(this.requestMappingHandlerMapping);
        return result;
    }

    @Override
    public String getVersion() {
        return "";
    }

    public String getPrefix(){
        return prefix;
    }

    public boolean getModify(){
        return modify;
    }
    @Override
    public void clearData() {
        clearFindFlag();
        this.prefix = null;
        modify = false;
        this.abstractUrlHandlerMapping = new HashMap<>();
        this.requestMappingHandlerMapping = new HashMap<>();
        springObjects = new HashSet<>();
    }
}
