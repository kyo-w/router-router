package com.kyodream.end.core.category;

import com.kyodream.end.analyse.*;
import com.kyodream.end.core.category.format.Format;
import com.kyodream.end.core.category.format.JettyFormat;
import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Component
@Slf4j
public class Jetty extends AbstractDataWrapper implements Handle {
    private static Set<String> filterClass = new HashSet<>();
    private static Set<String> blackList = new HashSet<String>();
    static {
        filterClass.add("org.eclipse.jetty.server.HttpChannel");
        blackList.add("org.eclipse.jetty.servlet.NoJspServlet");
    }


    private HashMap<String, String> maps = new HashMap<>();
    private MethodEntryEvent currentMethodEntryEvent;
    private String version;

    @Override
    public void handleEvent(EventSet eventSet, VirtualMachine attach) {
        for (Event event : eventSet) {
            if (event instanceof MethodEntryEvent) {
                currentMethodEntryEvent = (MethodEntryEvent) event;
                if (currentMethodEntryEvent.method().name().equals("getServer")) {
                    handleVersion(attach);
                    hasFind();
                    log.info("Jetty Handle");
                    ObjectReference objectReference = null;
                    try {
                        objectReference = currentMethodEntryEvent.thread().frame(0).thisObject();
                    } catch (IncompatibleThreadStateException e) {
                        e.printStackTrace();
                    }
                    if (objectReference != null) {
                        selectorHandle(objectReference, attach);
                    }
                }
            }
        }
    }

    private void handleVersion(VirtualMachine attach) {
        ObjectReference version1 = Utils.getStaticField(currentMethodEntryEvent.thread(), attach, "org.eclipse.jetty.util.Jetty", "VERSION");
        version = Format.doubleDot(version1.toString());
    }

    private void selectorHandle(ObjectReference objectReference, VirtualMachine virtualMachine) {
        ObjectReference connector = Utils.getFieldObject(objectReference, "_connector");
        ObjectReference server = Utils.getFieldObject(connector, "_server");
        ObjectReference handler = Utils.getFieldObject(server, "_handler");
        String handleName = null;
        handleName = Utils.getString(currentMethodEntryEvent.thread(), handler);
        if (handleName != null) {
            if (handleName.contains("ServletContextHandler")) {
                handlerServletContext(handler, "");
            } else if (handleName.contains("HandlerList")) {
                handleHandlerList(handler, virtualMachine);
            } else if (handleName.contains("WebAppContext")) {
                handleWebAppContext(handler, "");
            }
        }
    }

    private void handleWebAppContext(ObjectReference handler, String prefix) {
        String rawContextPath = Utils.getFieldObject(handler, "_contextPath").toString();
        String contextPath = JettyFormat.doubleDot(rawContextPath);
        ObjectReference servletHandler = Utils.getFieldObject(handler, "_servletHandler");
        ObjectReference servletNameMap = Utils.getFieldObject(servletHandler, "_servletPathMap");
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(currentMethodEntryEvent.thread(), servletNameMap);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            String rawUrl = next.getKey().toString();
            ObjectReference value = next.getValue();
            String rawClassName = Utils.getString(currentMethodEntryEvent.thread(), value);
            String url = JettyFormat.doubleDot(rawUrl);
            String className = JettyFormat.getClassName(rawClassName);
            String fullPath = JettyFormat.doubleSlash(prefix + contextPath + url);
            if(!blackList.contains(className)) {
                maps.put(fullPath, className);
            }
        }
    }

    private void handleHandlerList(ObjectReference handler, VirtualMachine attach) {
        ObjectReference beans = Utils.getFieldObject(handler, "_beans");
        List<ObjectReference> list = ListAnalyse.getArrayList(currentMethodEntryEvent.thread(), attach, beans, true);
        for (ObjectReference elem : list) {
            ObjectReference bean = Utils.getFieldObject(elem, "_bean");
            String objectType = Utils.getString(currentMethodEntryEvent.thread(), bean);
            if (objectType.contains("WebAppContext")) {
                handleWebAppContext(bean, "");
            } else if (objectType.contains("ServletContextHandler")) {
                handlerServletContext(bean, "");
            }
        }
    }

    private void handlerServletContext(ObjectReference handler, String prefix) {
        String rawContextPath = Utils.getFieldObject(handler, "_contextPath").toString();
        String contextPath = JettyFormat.doubleDot(rawContextPath);
        ObjectReference servletHandler = Utils.getFieldObject(handler, "_servletHandler");
        ArrayReference servletMappings = (ArrayReference) Utils.getFieldObject(servletHandler, "_servletMappings");
        for (int i = 0; i < servletMappings.length(); i++) {
            ObjectReference value = (ObjectReference) servletMappings.getValue(i);
            String urlAndClassName = Utils.getString(currentMethodEntryEvent.thread(), value);
            HashMap<String, String> stringStringHashMap = JettyFormat.splitUrlAndClassName(urlAndClassName, prefix + contextPath);
            Iterator<Map.Entry<String, String>> iterator = stringStringHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if(!blackList.contains(next.getValue())){
                    maps.put(next.getKey(), next.getValue());
                }
            }
        }
    }

    @Override
    public Set<String> getFilterClassName() {
        return filterClass;
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return maps;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        clearFindFlag();
        currentMethodEntryEvent = null;
        maps = new HashMap<>();
        version = "";
    }
}
