package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.ListAnalyse;
import com.kyodream.debugger.core.analyse.SetAnalyse;
import com.kyodream.debugger.core.analyse.Utils;
import com.kyodream.debugger.core.category.format.SpringFormat;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Spring默认使用SpringMvc加Controller注册的解决方案
 * 不考虑org.springframework.web.servlet.DispatcherServlet + struts / jersey的情况
 */
@Slf4j
@Component
public class Spring extends AbstractDataWrapper {

    private static Set<String> filterClass = new HashSet<>();

    static {
        filterClass.add("org.springframework.web.servlet.DispatcherServlet");
    }

    //    支持的注解
    //    primary router:@RestController/@Controller/@GetMapping/@PostMapping...
    private HashMap<String, String> requestMappingHandlerMapping = new HashMap<>();
    //    Implementation Controller interface,  use @Bean(name = "${/router}")
    private HashMap<String, String> beanNameUrlHandlerMapping = new HashMap<>();
    //    Implementation Controller interface, configuration the simpleUrlHandlerMapping
    private HashMap<String, String> simpleUrlHandlerMapping = new HashMap<>();
    private ThreadReference threadReference = null;
    private String prefix = "";
    private String version = "";
    private Set<ObjectReference> springObjects = new HashSet<>();
    @Autowired
    private DebugWebSocket debugWebSocket;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        debugWebSocket.sendInfo("发现spring对象");
        this.springObjects.addAll(objectReference);
    }

    @Override
    public void analystsObject(VirtualMachine attach) {
        handleVersion(attach);
        hasFind();
        debugWebSocket.sendInfo("开始分析spring");
        for (ObjectReference springObject : this.springObjects) {
            analysts(springObject);
        }
        debugWebSocket.sendInfo("分析spring结束");
    }

    public void analysts(ObjectReference objectReference) {
        getPrefix(objectReference);
        Field handlerMappings = objectReference.referenceType().fieldByName("handlerMappings");
        ObjectReference value = (ObjectReference) objectReference.getValue(handlerMappings);
        List<ObjectReference> list = ListAnalyse.getArrayList(value);
        try {
            for (ObjectReference elem : list) {
                String signature = elem.toString();
                if (signature.contains("RequestMappingHandlerMapping")) {
                    debugWebSocket.sendSuccess("发现Spring: RequestMappingHandlerMapping");
                    handleRequestMappingHandlerMapping(elem, debugWebSocket);
                } else if (signature.contains("BeanNameUrlHandlerMapping")) {
                    debugWebSocket.sendSuccess("发现Spring: BeanNameUrlHandlerMapping");
                    handleBeanNameUrlHandlerMappingOrSimpleUrlHandlerMapping(elem);
                } else if (signature.contains("SimpleUrlHandlerMapping")) {
                    debugWebSocket.sendSuccess("发现Spring: SimpleUrlHandlerMapping");
                    handleBeanNameUrlHandlerMappingOrSimpleUrlHandlerMapping(elem);
                }
            }
        } catch (Exception e) {
            debugWebSocket.sendFail("Spring分析异常");
            e.printStackTrace();
        }
    }

    private void getPrefix(ObjectReference objectReference) {
        ObjectReference config = Utils.getFieldObject(objectReference, "config");
        ObjectReference config1 = Utils.getFieldObject(config, "config");
        ObjectReference mappings = Utils.getFieldObject(config1, "mappings");
        List<ObjectReference> arrayList = ListAnalyse.getArrayList(mappings);
        String notStandardPrefix = SpringFormat.doubleDot(arrayList.get(0).toString());
        if (notStandardPrefix.endsWith("*")) {
            prefix = notStandardPrefix.substring(0, notStandardPrefix.length() - 1);
        }
    }

    private void handleRequestMappingHandlerMapping(ObjectReference elem, DebugWebSocket webSocket) {
        Field mappingRegistry = elem.referenceType().fieldByName("mappingRegistry");
        ObjectReference mappingRegistryObject = null;

        boolean isLt4 = false;
        try {
            mappingRegistryObject = (ObjectReference) elem.getValue(mappingRegistry);
        } catch (Exception e) {
            //        SpringMvc version < 4.1.0.RELEASE
            webSocket.sendInfo("目标框架属于低版本(version < 4.1.0.RELEASE)");
            isLt4 = true;
        }
        if (!isLt4) {
            /**
             * 高版本spring中的RequestMappingHandlerMapping结构
             *  registry(k,v) -> RequestMappingInfo
             *      k(RequestMappingInfo)
             *          patterns(TreeSet)
             */
            ObjectReference registryList = Utils.getFieldObject(mappingRegistryObject, "registry");
            Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(registryList);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference requestMappingInfoObject = next.getValue();
                ObjectReference handlerMethodObject = Utils.getFieldObject(requestMappingInfoObject, "handlerMethod");
                ObjectReference method = Utils.getFieldObject(handlerMethodObject, "method");
                ObjectReference clazz = Utils.getFieldObject(method, "clazz");
                String className = ((StringReference) Utils.getFieldObject(clazz, "name")).value();
                ObjectReference mappingObject = Utils.getFieldObject(requestMappingInfoObject, "mapping");
                ObjectReference pathPatternsCondition = Utils.getFieldObject(mappingObject, "pathPatternsCondition");
                try {
                    ObjectReference patterns = Utils.getFieldObject(pathPatternsCondition, "patterns");
                    ArrayList<ObjectReference> treeSet = SetAnalyse.getTreeSet(patterns);
                    Iterator<ObjectReference> treeSetIterator = treeSet.iterator();
                    while (treeSetIterator.hasNext()) {
                        ObjectReference pathPattern = treeSetIterator.next();
                        StringReference patternString = (StringReference) Utils.getFieldObject(pathPattern, "patternString");
                        String url = SpringFormat.doubleSlash(prefix + patternString.value());
                        requestMappingHandlerMapping.put(url, className);
                    }
                } catch (Exception e) {
                    debugWebSocket.sendInfo("pathPatternsCondition无目标");
                    ObjectReference patternsCondition = Utils.getFieldObject(mappingObject, "patternsCondition");
                    ObjectReference patterns = Utils.getFieldObject(patternsCondition, "patterns");
                    ArrayList<ObjectReference> treeSet = SetAnalyse.getHashSetObject(patterns);
                    Iterator<ObjectReference> iterator1 = treeSet.iterator();
                    while (iterator1.hasNext()) {
                        ObjectReference next1 = iterator1.next();
                        String url = ((StringReference) next1).value();
                        requestMappingHandlerMapping.put(url, className);
                    }
                }
            }
        } else {
            handleRequestMappingHandlerMappingLt4_0(elem);
        }
    }

    private void handleRequestMappingHandlerMappingLt4_0(ObjectReference elem) {
        Field handlerMethods = elem.referenceType().fieldByName("handlerMethods");
        ObjectReference handlerMethodsObject = (ObjectReference) elem.getValue(handlerMethods);
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(handlerMethodsObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference key = next.getKey();
            ObjectReference value = next.getValue();

            ObjectReference method = Utils.getFieldObject(value, "method");
            ObjectReference clazz = Utils.getFieldObject(method, "clazz");
            String className = ((StringReference) Utils.getFieldObject(clazz, "name")).value();

            ObjectReference patternsCondition = Utils.getFieldObject(key, "patternsCondition");
            ObjectReference patterns = Utils.getFieldObject(patternsCondition, "patterns");
            ArrayList<ObjectReference> unmodifiableSet = SetAnalyse.getUnmodifiableSet(patterns);
            Iterator<ObjectReference> iterator1 = unmodifiableSet.iterator();
            while (iterator1.hasNext()) {
                String url = ((StringReference) iterator1.next()).value();
                String fullUrl = SpringFormat.doubleSlash(prefix + url);
                requestMappingHandlerMapping.put(fullUrl, className);
            }
        }
    }

    private void handleBeanNameUrlHandlerMappingOrSimpleUrlHandlerMapping(ObjectReference elem) {
        Field handlerMap = elem.referenceType().fieldByName("handlerMap");
        ObjectReference handlerMapObject = (ObjectReference) elem.getValue(handlerMap);
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            String url = ((StringReference) next.getKey()).value();
            ObjectReference value = next.getValue();
            String className = value.referenceType().name();
            String fullUrl = SpringFormat.doubleSlash(prefix + url);
            beanNameUrlHandlerMapping.put(fullUrl, className);
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> result = new HashMap<>();
        result.putAll(requestMappingHandlerMapping);
        result.putAll(beanNameUrlHandlerMapping);
        result.putAll(simpleUrlHandlerMapping);
        return result;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        clearFindFlag();
        this.requestMappingHandlerMapping = new HashMap<>();
        this.beanNameUrlHandlerMapping = new HashMap<>();
        this.simpleUrlHandlerMapping = new HashMap<>();
        this.threadReference = null;
        this.prefix = "";
        this.version = "";
    }

    @Override
    public Set<String> getDiscoveryClass() {
        return filterClass;
    }

    private void handleVersion(VirtualMachine attach) {
        version = "未知版本";
    }

}
