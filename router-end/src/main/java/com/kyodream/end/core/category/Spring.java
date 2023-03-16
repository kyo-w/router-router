package com.kyodream.end.core.category;

import com.kyodream.end.analyse.*;
import com.kyodream.end.core.category.format.SpringFormat;
import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class Spring extends AbstractDataWrapper implements Handle {

    private static Set<String> filterClass = new HashSet<>();
    static {
        filterClass.add("org.springframework.web.servlet.DispatcherServlet");
    }

    //    primary router:@RestController/@Controller/@GetMapping/@PostMapping...
    private HashMap<String, String> requestMappingHandlerMapping = new HashMap<>();
    //    Implementation Controller interface,  use @Bean(name = "${/router}")
    private HashMap<String, String> beanNameUrlHandlerMapping = new HashMap<>();
    //    Implementation Controller interface, configuration the simpleUrlHandlerMapping
    private HashMap<String, String> simpleUrlHandlerMapping = new HashMap<>();
    private MethodEntryEvent currentEntryEvent;
    private String prefix = "";
    private String version = "";

    public void handleEvent(EventSet eventSet, VirtualMachine attach) {
        for (Event event : eventSet) {
            if (event instanceof MethodEntryEvent) {
                currentEntryEvent = (MethodEntryEvent) event;
                if (currentEntryEvent.method().name().equals("doService")) {
                    handleVersion(attach);
                    hasFind();
                    ObjectReference objectReference = null;
                    try {
                        objectReference = currentEntryEvent.thread().frame(0).thisObject();
                    } catch (IncompatibleThreadStateException e) {
                        throw new RuntimeException(e);
                    }

                    analy(objectReference, attach);
                }
            }
        }
    }
    private void handleVersion(VirtualMachine attach) {
        ObjectReference getVersion = Utils.invokeStaticMethod(currentEntryEvent.thread(), attach, "org.springframework.core.SpringVersion", "getVersion");
        version = SpringFormat.doubleDot(getVersion.toString());
    }
    public void analy(ObjectReference objectReference, VirtualMachine attach) {
        getPrefix(objectReference, attach);
        Field handlerMappings = objectReference.referenceType().fieldByName("handlerMappings");
        ObjectReference value = (ObjectReference) objectReference.getValue(handlerMappings);
        List<ObjectReference> list = ListAnalyse.getArrayList(currentEntryEvent.thread(), attach, value, false);
        try {
            for (ObjectReference elem : list) {
                String signature = elem.toString();
                if (signature.contains("RequestMappingHandlerMapping")) {
                    log.info("Spring: RequestMappingHandlerMapping");
                    handleRequestMappingHandlerMapping(elem);
                } else if (signature.contains("BeanNameUrlHandlerMapping")) {
                    log.info("Spring: BeanNameUrlHandlerMapping");
                    handleBeanNameUrlHandlerMapping(elem);
                } else if (signature.contains("SimpleUrlHandlerMapping")) {
                    log.info("Spring: SimpleUrlHandlerMapping");
                    handleSimpleUrlHandlerMapping(elem);
                }
            }
        } catch (Exception e) {
//                        异常日志
            log.error("spring收集Api异常");
        }
    }
    private void getPrefix(ObjectReference objectReference, VirtualMachine attach) {
        ObjectReference config = Utils.getFieldObject(objectReference, "config");
        ObjectReference config1 = Utils.getFieldObject(config, "config");
        ObjectReference mappings = Utils.getFieldObject(config1, "mappings");
        List<ObjectReference> arrayList = ListAnalyse.getArrayList(currentEntryEvent.thread(), attach, mappings, false);
        String notStandardPrefix = SpringFormat.doubleDot(arrayList.get(0).toString());
        if (notStandardPrefix.endsWith("*")) {
            prefix = notStandardPrefix.substring(0, notStandardPrefix.length() - 1);
        }
    }

    //mappingRegistry
    //  registry
    //      RequestMappingInfo
    private void handleRequestMappingHandlerMapping(ObjectReference elem) {
        Field mappingRegistry = elem.referenceType().fieldByName("mappingRegistry");
        Field handlerMethods = elem.referenceType().fieldByName("handlerMethods");
        ObjectReference mappingRegistryObject = null;

        boolean isLt4 = false;
        try {
            mappingRegistryObject = (ObjectReference) elem.getValue(mappingRegistry);
        } catch (Exception e) {
            //        SpringMvc version < 4.1.0.RELEASE
            log.warn("目标框架属于低版本(version < 4.1.0.RELEASE)");
            isLt4 = true;
        }
        if (!isLt4) {
            Field registry = mappingRegistryObject.referenceType().fieldByName("registry");
            ObjectReference registryList = (ObjectReference) mappingRegistryObject.getValue(registry);
            Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentEntryEvent.thread(), registryList);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference requestMappingInfoObject = next.getValue();

                Field mapping = requestMappingInfoObject.referenceType().fieldByName("mapping");
                ObjectReference mappingObject = (ObjectReference) requestMappingInfoObject.getValue(mapping);
                String rawurl = Utils.getString(currentEntryEvent.thread(), mappingObject);
                String requestUrl = SpringFormat.getRequestMappingUrl(rawurl);

                Field handlerMethod = requestMappingInfoObject.referenceType().fieldByName("handlerMethod");
                ObjectReference handlerMethodObject = (ObjectReference) requestMappingInfoObject.getValue(handlerMethod);
                String classNameRaw = Utils.getString(currentEntryEvent.thread(), handlerMethodObject);
                String className = SpringFormat.doubleDot(classNameRaw);
                String fullUrl = SpringFormat.doubleSlash(prefix + requestUrl);
                requestMappingHandlerMapping.put(fullUrl, className);
            }
        } else {
            handleRequestMappingHandlerMappingLt4_0(elem);
        }
    }

    //handlerMethods
    private void handleRequestMappingHandlerMappingLt4_0(ObjectReference elem) {
        Field handlerMethods = elem.referenceType().fieldByName("handlerMethods");
        ObjectReference handlerMethodsObject = (ObjectReference) elem.getValue(handlerMethods);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentEntryEvent.thread(), handlerMethodsObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference key = next.getKey();
            ObjectReference value = next.getValue();
            String rawUrl = Utils.getString(currentEntryEvent.thread(), key);
            String url = SpringFormat.doubleDot(rawUrl);
            String className = Utils.getString(currentEntryEvent.thread(), value);
            String fullUrl = SpringFormat.doubleSlash(prefix + url);
            requestMappingHandlerMapping.put(fullUrl, className);
        }
    }
    private void handleBeanNameUrlHandlerMapping(ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field handlerMap = elem.referenceType().fieldByName("handlerMap");
        ObjectReference handlerMapObject = (ObjectReference) elem.getValue(handlerMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentEntryEvent.thread(), handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            String rawUrl = next.getKey().toString();
            String url = SpringFormat.doubleDot(rawUrl);

            ObjectReference value = next.getValue();
            Method getClass = value.referenceType().methodsByName("getClass").get(0);
            String rawClassName = value.invokeMethod(currentEntryEvent.thread(), getClass, Collections.EMPTY_LIST, 0).toString();
            String className = SpringFormat.getClassName(rawClassName);
            String fullUrl = SpringFormat.doubleSlash(prefix + url);
            beanNameUrlHandlerMapping.put(fullUrl, className);
        }
    }
    private void handleSimpleUrlHandlerMapping(ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field handlerMap = elem.referenceType().fieldByName("handlerMap");
        ObjectReference handlerMapObject = (ObjectReference) elem.getValue(handlerMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentEntryEvent.thread(), handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            String rawUrl = next.getKey().toString();
            String url = SpringFormat.doubleDot(rawUrl);

            ObjectReference value = next.getValue();
            Method getClass = value.referenceType().methodsByName("getClass").get(0);
            String rawClassName = value.invokeMethod(currentEntryEvent.thread(), getClass, Collections.EMPTY_LIST, 0).toString();
            String className = SpringFormat.getClassName(rawClassName);
            String fullUrl = SpringFormat.doubleSlash(prefix + url);
            simpleUrlHandlerMapping.put(fullUrl, className);
        }
    }
    @Override
    public Set<String> getFilterClassName() {
        return filterClass;
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
        this.currentEntryEvent = null;
        this.prefix = "";
        this.version = "";
    }

}
