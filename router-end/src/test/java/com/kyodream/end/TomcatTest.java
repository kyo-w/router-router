package com.kyodream.end;

import com.kyodream.end.analyse.*;
import com.kyodream.end.analyse.*;
import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TomcatTest {

    private static HashMap<String, String> exactWrappersMap = new HashMap<>();
    private static HashMap<String, String> wildcardWrappersMap = new HashMap<>();
    private static HashMap<String, String> extensionWrappersMap = new HashMap<>();

    private static ObjectReference jerseyRoot = null;
    private static MethodEntryEvent currentMethodEntryEvent;

    public static void main(String[] args) throws Exception {
        VirtualMachine attach = TestUtils.getAttach();
        if (attach == null) {
            System.out.println("failed");
            System.exit(-1);
        }
        TestThread debugThread = new TestThread(attach);
        Thread thread = new Thread(debugThread);
        thread.setDaemon(true);
        thread.start();

        attach.suspend();
        EventRequestManager eventRequestManager = attach.eventRequestManager();
        MethodEntryRequest methodEntryRequest = eventRequestManager.createMethodEntryRequest();
        methodEntryRequest.addClassFilter("org.apache.catalina.mapper.Mapper");
//        methodEntryRequest.addClassFilter("org.apache.tomcat.util.http.mapper.Mapper");
        methodEntryRequest.enable();
        attach.resume();
        EventQueue eventQueue = attach.eventQueue();
        EventSet eventSet = null;
        while ((eventSet = eventQueue.remove()) != null) {
            handleEvent(eventSet, attach);
            eventSet.resume();
        }
    }

    private static void handleEvent(EventSet eventSet, VirtualMachine attach) throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
        for (Event event : eventSet) {
            if (event instanceof MethodEntryEvent) {
                currentMethodEntryEvent = (MethodEntryEvent) event;
                if (currentMethodEntryEvent.method().name().equals("map")) {
                    String name = ((MethodEntryEvent) event).thread().frame(0).thisObject().referenceType().name();
                    if (name.equals("org.apache.catalina.mapper.Mapper")) {
                        handleTomcat98();
                    } else if (name.equals("org.apache.tomcat.util.http.mapper.Mapper")) {
                        handleTomcat76();
                    }
                }
            }
        }
    }

    private static void handleTomcat98() throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
        ObjectReference objectReference = currentMethodEntryEvent.thread().frame(0).thisObject();
        Field contextObjectToContextVersionMap = objectReference.referenceType().fieldByName("contextObjectToContextVersionMap");
        ObjectReference containList = (ObjectReference) objectReference.getValue(contextObjectToContextVersionMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentMethodEntryEvent.thread(), containList);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();

            Boolean jersey = new Jersey().isJersey(currentMethodEntryEvent, next.getKey());
            if (jersey) {
                jerseyRoot = next.getKey();
            }

            ObjectReference value = next.getValue();
            Field path = value.referenceType().fieldByName("path");
            String prefix = value.getValue(path).toString().replace("\"", "");
            handleCategory(prefix, value);
        }
        printMap();
    }

    private static void handleTomcat76() throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
        ObjectReference objectReference = currentMethodEntryEvent.thread().frame(0).thisObject();
//        Multi-host need iter
        ArrayReference hosts = (ArrayReference) Utils.getFieldObject(objectReference, "hosts");
        for (int i = 0; i < hosts.length(); i++) {
            ObjectReference mapperHost = (ObjectReference) hosts.getValue(i);
            ObjectReference contextList = Utils.getFieldObject(mapperHost, "contextList");
            ArrayReference contexts = (ArrayReference) Utils.getFieldObject(contextList, "contexts");
            for (int j = 0; j < contexts.length(); j++) {
                ObjectReference contextOne = (ObjectReference) contexts.getValue(i);
                ArrayReference versions = (ArrayReference) Utils.getFieldObject(contextOne, "versions");
                for (int z = 0; z < versions.length(); z++) {
                    ObjectReference context = (ObjectReference) versions.getValue(z);
                    ObjectReference path = Utils.getFieldObject(context, "path");
                    String prefix = path.toString().replace("\"", "");
                    handleCategory(prefix, context);
                }
            }
        }
        printMap();
    }

    private static void handleCategory(String prefix, ObjectReference root) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field defaultWrapper = root.referenceType().fieldByName("defaultWrapper");
        Field exactWrappers = root.referenceType().fieldByName("exactWrappers");
        Field wildcardWrappers = root.referenceType().fieldByName("wildcardWrappers");
        Field extensionWrappers = root.referenceType().fieldByName("extensionWrappers");
        handleDefaultWrapper((ObjectReference) root.getValue(defaultWrapper));
        handleExactWrappers(prefix, (ArrayReference) root.getValue(exactWrappers));
        handleWildcardWrappers(prefix, (ArrayReference) root.getValue(wildcardWrappers));
        handleExtensionWrappers(prefix, (ArrayReference) root.getValue(extensionWrappers));
    }

    private static void handleDefaultWrapper(ObjectReference defaultWrapper) {
        ObjectReference object = Utils.getFieldObject(defaultWrapper, "object");
        ObjectReference servletClass = Utils.getFieldObject(object, "servletClass");
        System.out.println("/");
        System.out.println(servletClass);
    }

    private static void handleExactWrappers(String prefix, ArrayReference exactWrappers) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, exactWrappers, exactWrappersMap);
    }

    private static void handleWildcardWrappers(String prefix, ArrayReference wildcardWrappers) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, wildcardWrappers, wildcardWrappersMap);
    }

    private static void handleExtensionWrappers(String prefix, ArrayReference extensionWrappers) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, extensionWrappers, extensionWrappersMap);
    }

    private static void handleMapperWrappers(String prefix, ArrayReference objectReference, HashMap<String, String> originMap) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        int length = objectReference.length();
        for (int i = 0; i < length; i++) {
            ObjectReference elem = (ObjectReference) objectReference.getValue(i);
            String url = Utils.getFieldObject(elem, "name").toString().replace("\"", "");
            ObjectReference object = Utils.getFieldObject(elem, "object");
            String servletClass = Utils.getFieldObject(object, "servletClass").toString().replace("\"", "");
            if (servletClass.equals(Jersey.JerseyName)) {
                new Jersey().handleJersey(currentMethodEntryEvent, jerseyRoot, prefix);
            }
            originMap.put(prefix + url, servletClass.replace("\"", ""));
        }
    }

    private static void printMap() {
        Iterator<Map.Entry<String, String>> iterator = exactWrappersMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println("url: " + next.getKey() + ", class: " + next.getValue());
        }
    }
}
