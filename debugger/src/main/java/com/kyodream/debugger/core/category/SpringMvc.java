package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.ListAnalyse;
import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.SetAnalyse;
import com.kyodream.debugger.core.category.format.Format;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.VirtualMachine;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SpringMvc extends DefaultFramework {
    private HashMap<String, String> abstractUrlHandlerMapping = new HashMap<>();

    private HashMap<String, String> requestMappingHandlerMapping = new HashMap<>();
    @Override
    public boolean analystsFrameworkObject(VirtualMachine vm) {
        if (getPrefix() == null) {
            debugWebSocket.sendInfo("spring还未获取路由前缀，先跳过");
            return false;
        }
        debugWebSocket.sendInfo("开始分析spring");
        for (ObjectReference springObject : getAnalystsObject()) {
            ObjectReference handlerMappingsRef = getFieldObject(springObject, "handlerMappings");
            List<ObjectReference> arrayListRef = ListAnalyse.getArrayList(handlerMappingsRef);
            for (ObjectReference arrayElem : arrayListRef) {
                String name = arrayElem.referenceType().name();
                if (name.equals("org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping") ||
                        name.equals("org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping") ||
                        name.equals("org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping") ||
                        name.equals("org.springframework.web.servlet.handler.SimpleUrlHandlerMapping")) {

                    handlerAbstractUrlHandlerMapping(arrayElem);
                }
                if (name.equals("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")) {
                    handlerRequestMappingHandlerMapping(arrayElem);
                }
            }
        }
        debugWebSocket.sendSuccess("分析spring结束");
        return true;
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
                                if (!getModify()) {
                                    String fullUrl = getPrefix().replace("*", "/" + url);
                                    requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                                } else {
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
                                    if (!getModify()) {
                                        String fullUrl = getPrefix().replace("*", "/" + ((StringReference) urlStringRef).value());
                                        requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                                    } else {
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
                        if (!getModify()) {
                            String fullUrl = getPrefix().replace("*", "/" + patternString.value());
                            requestMappingHandlerMapping.put(Format.doubleSlash(fullUrl), className);
                        } else {
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
                    if (!modify) {
                        String fullUrl = getPrefix().replace("*", "/" + url);
                        requestMappingHandlerMapping.put(fullUrl, className);
                    } else {
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
            if (!modify) {
                String fullUrl = getPrefix().replace("*", "/" + url);
                abstractUrlHandlerMapping.put(Format.doubleSlash(fullUrl), className);
            } else {
                abstractUrlHandlerMapping.put(url, className);
            }
        }
    }

    @Override
    public void setHandleOrFrameworkName() {
        this.handleOrFrameworkName = "spring";
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> dataWrapper = super.getDataWrapper();
        dataWrapper.putAll(this.abstractUrlHandlerMapping);
        dataWrapper.putAll(this.requestMappingHandlerMapping);
        return dataWrapper;
    }

    public boolean getModify() {
        return modify;
    }

    @Override
    public void clearAny() {
        super.clearAny();
        modify = false;
        this.abstractUrlHandlerMapping.clear();
        this.requestMappingHandlerMapping.clear();
    }
}
