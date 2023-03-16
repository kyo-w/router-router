package com.kyodream.end;

import com.kyodream.end.analyse.HashMapAnalyse;
import com.kyodream.end.analyse.ListAnalyse;
import com.kyodream.end.analyse.Utils;
import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.tools.jdi.SocketAttachingConnector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpringTest {
    private static String hostname = "127.0.0.1";
    private static String port = "5005";

    public static void main(String[] args) throws Exception {
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue(hostname);
        argumentHashMap.get("port").setValue(port);
        argumentHashMap.get("timeout").setValue("3000");
        VirtualMachine attach = null;
        try {
            attach = socketAttachingConnector.attach(argumentHashMap);
        } catch (IllegalConnectorArgumentsException | IOException e) {
        }
        attach.suspend();

        EventRequestManager eventRequestManager = attach.eventRequestManager();
        MethodEntryRequest methodEntryRequest = eventRequestManager.createMethodEntryRequest();
        methodEntryRequest.addClassFilter("org.springframework.web.servlet.DispatcherServlet");
        methodEntryRequest.enable();
        attach.resume();
        EventQueue eventQueue = attach.eventQueue();
        EventSet eventSet = null;
        while (true) {
            while ((eventSet = eventQueue.remove()) != null) {
                handleEvent(eventSet, attach);
            }
        }
    }

    public static void handleEvent(EventSet eventSet, VirtualMachine attach) throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
        for (Event event : eventSet) {
            if (event instanceof MethodEntryEvent) {
                MethodEntryEvent methodEntryEvent = (MethodEntryEvent) event;
                if (methodEntryEvent.method().name().equals("doService")) {
                    ObjectReference objectReference = methodEntryEvent.thread().frame(0).thisObject();
                    Field handlerMappings = objectReference.referenceType().fieldByName("handlerMappings");
                    ObjectReference value = (ObjectReference) objectReference.getValue(handlerMappings);
                    List<ObjectReference> list = ListAnalyse.getArrayList(methodEntryEvent.thread(), attach, value, false);
                    for (ObjectReference elem : list) {
                        String signature = elem.toString();
                        if (signature.contains("RequestMappingHandlerMapping")) {
                            System.out.println("------------");
                            System.out.println("RequestMappingHandlerMapping");
                            handleRequestMappingHandlerMapping(attach, methodEntryEvent.thread(), elem);
                            System.out.println("------------");
                        } else if (signature.contains("BeanNameUrlHandlerMapping")) {
                            System.out.println("------------");
                            System.out.println("BeanNameUrlHandlerMapping");
                            handleBeanNameUrlHandlerMapping(attach, methodEntryEvent.thread(), elem);
                            System.out.println("------------");
                        } else if (signature.contains("SimpleUrlHandlerMapping")) {
                            System.out.println("------------");
                            System.out.println("SimpleUrlHandlerMapping");
                            handleBeanNameUrlHandlerMapping(attach, methodEntryEvent.thread(), elem);
                            System.out.println("------------");
                        }
                    }
                }
            }
            attach.resume();
        }
    }


    //mappingRegistry
    //  registry
    //      RequestMappingInfo
    //
    //
    //
    private static void handleRequestMappingHandlerMapping(VirtualMachine attach, ThreadReference thread, ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field mappingRegistry = elem.referenceType().fieldByName("mappingRegistry");
        Field handlerMethods = elem.referenceType().fieldByName("handlerMethods");
        ObjectReference mappingRegistryObject = null;

        boolean isLt4  = false;
        try {
            mappingRegistryObject = (ObjectReference) elem.getValue(mappingRegistry);
        } catch (Exception e) {
        //        SpringMvc version < 4.1.0.RELEASE
            isLt4 = true;
        }
        if(!isLt4) {
            Field registry = mappingRegistryObject.referenceType().fieldByName("registry");
            ObjectReference registryList = (ObjectReference) mappingRegistryObject.getValue(registry);
            Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(thread, registryList);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference requestMappingInfoObject = next.getValue();


                Field mapping = requestMappingInfoObject.referenceType().fieldByName("mapping");
                ObjectReference mappingObject = (ObjectReference) requestMappingInfoObject.getValue(mapping);
                String url = Utils.getString(thread, mappingObject);
                System.out.println(url);

                Field handlerMethod = requestMappingInfoObject.referenceType().fieldByName("handlerMethod");
                ObjectReference handlerMethodObject = (ObjectReference) requestMappingInfoObject.getValue(handlerMethod);
                String string = Utils.getString(thread, handlerMethodObject);
                System.out.println(string);
            }
        }else{
            handleRequestMappingHandlerMappingLt4_0(attach, thread, elem);
        }
    }

    private static void handleRequestMappingHandlerMappingLt4_0(VirtualMachine attach, ThreadReference thread, ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field handlerMethods = elem.referenceType().fieldByName("handlerMethods");
        ObjectReference handlerMethodsObject = (ObjectReference) elem.getValue(handlerMethods);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(thread, handlerMethodsObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference key = next.getKey();
            ObjectReference value = next.getValue();
            String string = Utils.getString(thread, key);
            System.out.println(string);
            String string1 = Utils.getString(thread, value);
            System.out.println(string1);
        }
    }

    private static void handleBeanNameUrlHandlerMapping(VirtualMachine attach, ThreadReference thread, ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field handlerMap = elem.referenceType().fieldByName("handlerMap");
        ObjectReference handlerMapObject = (ObjectReference) elem.getValue(handlerMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(thread, handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            System.out.println(next.getKey());
            ObjectReference value = next.getValue();
            Method getClass = value.referenceType().methodsByName("getClass").get(0);
            Value value1 = value.invokeMethod(thread, getClass, Collections.EMPTY_LIST, 0);
            System.out.println(value1);
        }
    }

    private static void handleSimpleUrlHandlerMapping(VirtualMachine attach, ThreadReference thread, ObjectReference elem) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field handlerMap = elem.referenceType().fieldByName("handlerMap");
        ObjectReference handlerMapObject = (ObjectReference) elem.getValue(handlerMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(thread, handlerMapObject);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            System.out.println(next.getKey());
            ObjectReference value = next.getValue();
            Method getClass = value.referenceType().methodsByName("getClass").get(0);
            Value value1 = value.invokeMethod(thread, getClass, Collections.EMPTY_LIST, 0);
            System.out.println(value1);
        }
    }

}
