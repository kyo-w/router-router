package com.kyodream.end.core.category;

import com.kyodream.end.analyse.*;
import com.kyodream.end.analyse.*;
import com.kyodream.end.core.category.format.Format;
import com.kyodream.end.core.category.format.SpringFormat;
import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Tomcat needs to complete the forwarding of multiple routes
 * Jersey
 * Struts
 */
@Component
@Slf4j
public class Tomcat extends AbstractDataWrapper implements Handle {
    private static Set<String> filterClass = new HashSet<>();

    static {
        filterClass.add("org.apache.catalina.mapper.Mapper");
        filterClass.add("org.apache.tomcat.util.http.mapper.Mapper");
    }

    @Autowired
    private JerseyHandle jerseyHandle;
    @Autowired
    private Struts struts;

    private String classOfDefaultUrl = "";
    private HashMap<String, String> exactWrappersMap = new HashMap<>();
    private HashMap<String, String> wildcardWrappersMap = new HashMap<>();
    private HashMap<String, String> extensionWrappersMap = new HashMap<>();
    private HashMap<String, String> defaultMap = new HashMap<>();
    private static ObjectReference jerseyRoot = null;
    private static MethodEntryEvent currentMethodEntryEvent;
    private String version;

    public void handleEvent(EventSet eventSet, VirtualMachine attach) {
        for (Event event : eventSet) {
            if (event instanceof MethodEntryEvent) {
                currentMethodEntryEvent = (MethodEntryEvent) event;
                if (currentMethodEntryEvent.method().name().equals("map")) {
                    handleVersion(attach);
                    hasFind();
                    String name = null;
                    try {
                        name = ((MethodEntryEvent) event).thread().frame(0).thisObject().referenceType().name();
                    } catch (IncompatibleThreadStateException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (name.equals("org.apache.catalina.mapper.Mapper")) {
                            log.info("Tomcat9/8 Handle");
                            handleTomcat98();
                        } else if (name.equals("org.apache.tomcat.util.http.mapper.Mapper")) {
                            log.info("Tomcat7/6 Handle");
                            handleTomcat76();
                        }
                    } catch (Exception e) {
//                        错误异常
                    }
                }
            }
        }
    }

    private void handleVersion(VirtualMachine attach) {
        ObjectReference getServerInfo = Utils.invokeStaticMethod(currentMethodEntryEvent.thread(), attach, "org.apache.catalina.util.ServerInfo", "getServerInfo");
        version = Format.doubleDot(getServerInfo.toString());
    }

    private void handleTomcat98() throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
        ObjectReference objectReference = currentMethodEntryEvent.thread().frame(0).thisObject();
        Field contextObjectToContextVersionMap = null;
        ObjectReference containList = null;
        contextObjectToContextVersionMap = objectReference.referenceType().fieldByName("contextObjectToContextVersionMap");
        containList = (ObjectReference) objectReference.getValue(contextObjectToContextVersionMap);
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentMethodEntryEvent.thread(), containList);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            Boolean jersey = jerseyHandle.isJersey(currentMethodEntryEvent, next.getKey());
            if (jersey) {
                jerseyRoot = next.getKey();
            }

            struts.handleStruts(currentMethodEntryEvent, next.getKey());

            ObjectReference value = next.getValue();
            Field path = value.referenceType().fieldByName("path");
            String prefix = value.getValue(path).toString().replace("\"", "");
            handleCategory(prefix, value);
        }
    }

    private void handleTomcat76() throws IncompatibleThreadStateException, ClassNotLoadedException, InvocationException, InvalidTypeException {
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
    }

    private void handleCategory(String prefix, ObjectReference root)
            throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Field defaultWrapper = root.referenceType().fieldByName("defaultWrapper");
        Field exactWrappers = root.referenceType().fieldByName("exactWrappers");
        Field wildcardWrappers = root.referenceType().fieldByName("wildcardWrappers");
        Field extensionWrappers = root.referenceType().fieldByName("extensionWrappers");
        handleDefaultWrapper((ObjectReference) root.getValue(defaultWrapper));
        handleExactWrappers(prefix, (ArrayReference) root.getValue(exactWrappers));
        handleWildcardWrappers(prefix, (ArrayReference) root.getValue(wildcardWrappers));
        handleExtensionWrappers(prefix, (ArrayReference) root.getValue(extensionWrappers));
    }

    private void handleDefaultWrapper(ObjectReference defaultWrapper) {
        ObjectReference object = Utils.getFieldObject(defaultWrapper, "object");
        ObjectReference servletClass = Utils.getFieldObject(object, "servletClass");
        defaultMap.put("/", servletClass.toString().replace("\"", ""));
    }

    private void handleExactWrappers(String prefix, ArrayReference exactWrappers)
            throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, exactWrappers, exactWrappersMap);
    }

    private void handleWildcardWrappers(String prefix, ArrayReference wildcardWrappers)
            throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, wildcardWrappers, wildcardWrappersMap);
    }

    private void handleExtensionWrappers(String prefix, ArrayReference extensionWrappers)
            throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        handleMapperWrappers(prefix, extensionWrappers, extensionWrappersMap);
    }

    private void handleMapperWrappers(String prefix, ArrayReference objectReference, HashMap<String, String> originMap)
            throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        int length = objectReference.length();
        for (int i = 0; i < length; i++) {
            ObjectReference elem = (ObjectReference) objectReference.getValue(i);
            String url = Utils.getFieldObject(elem, "name").toString().replace("\"", "");
            ObjectReference object = Utils.getFieldObject(elem, "object");
            String servletClass = Utils.getFieldObject(object, "servletClass").toString().replace("\"", "");
            if (servletClass.equals(JerseyHandle.JerseyName)) {
                new JerseyHandle().handleJersey(currentMethodEntryEvent, jerseyRoot, prefix);
            }

            originMap.put(prefix + url, servletClass.replace("\"", ""));
        }
    }

    @Override
    public Set<String> getFilterClassName() {
        return filterClass;
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> resultHashMap = new HashMap<>();

        resultHashMap.putAll(defaultMap);
        resultHashMap.putAll(exactWrappersMap);
        resultHashMap.putAll(wildcardWrappersMap);
        resultHashMap.putAll(exactWrappersMap);

        return resultHashMap;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        this.version = "";
        clearFindFlag();
        this.classOfDefaultUrl = "";
        this.defaultMap = new HashMap<>();
        this.jerseyRoot = null;
        this.currentMethodEntryEvent = null;
        this.exactWrappersMap = new HashMap<>();
        this.wildcardWrappersMap = new HashMap<>();
        this.exactWrappersMap = new HashMap<>();
    }
}
