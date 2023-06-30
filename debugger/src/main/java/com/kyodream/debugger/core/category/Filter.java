package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.ListAnalyse;
import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.sun.jdi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Tomcat Filter
 */
@Component
public class Filter extends AbstractDataWrapper {
    private static Set<String> discoveryClass = new HashSet<>();

    static {
        discoveryClass.add("org.apache.catalina.mapper.Mapper");
        discoveryClass.add("org.eclipse.jetty.webapp.WebAppContext");
        discoveryClass.add("org.eclipse.jetty.servlet.ServletContextHandler");
//        不支持tomcat76
//        discoveryClass.add("org.apache.tomcat.util.http.mapper.Mapper");
    }

    @Autowired
    Struts struts;

    private Set<ObjectReference> tomcatOrJettyObjects = new HashSet<>();

    /**
     * k->(s->(v))
     * k: webapp path
     * s: filter ClassName
     * v: urlPatterns
     */
    private HashMap<String, LinkedHashMap<String, Set<String>>> filterMap = new HashMap<>();

    @Override
    public Set<String> getDiscoveryClass() {
        return discoveryClass;
    }


    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        tomcatOrJettyObjects.addAll(objectReference);
    }

    @Override
    public boolean analystsObject(VirtualMachine attach) {
        for (ObjectReference tomcatOrJettyObjectRef : tomcatOrJettyObjects) {
            if (tomcatOrJettyObjectRef.referenceType().name().equals("org.apache.catalina.mapper.Mapper")) {
                debugWebSocket.sendInfo("分析tomcat过滤器");
                handleTomcat98(tomcatOrJettyObjectRef);
                debugWebSocket.sendSuccess("完成过滤器分析");
            }
            if (tomcatOrJettyObjectRef.referenceType().name().equals("org.eclipse.jetty.webapp.WebAppContext")
                    || tomcatOrJettyObjectRef.referenceType().name().equals("org.eclipse.jetty.servlet.ServletContextHandler")) {
                debugWebSocket.sendInfo("分析jetty过滤器");
                handlerWebAppContextOrServletContext(tomcatOrJettyObjectRef);
                debugWebSocket.sendSuccess("完成过滤器分析");
            }
        }
        handlerStruts2();
        if (struts.getPrefix() != null) {
            struts.startAnalysts(attach);
        }
        return true;
    }

    private void handlerWebAppContextOrServletContext(ObjectReference tomcatOrJettyObjectRef) {
        StringReference contextPathRef = (StringReference) getFieldObject(tomcatOrJettyObjectRef, "_contextPath");
        this.filterMap.put(contextPathRef.value(), new LinkedHashMap<>());
        ObjectReference servletHandlerRef = getFieldObject(tomcatOrJettyObjectRef, "_servletHandler");
        ArrayReference filterPathMappingsRef = (ArrayReference) getFieldObject(servletHandlerRef, "_filterMappings");
        if (filterPathMappingsRef == null) {
            return;
        }
        for (Value arrayElemRefRaw : filterPathMappingsRef.getValues()) {
            ObjectReference arrayElem = (ObjectReference) arrayElemRefRaw;
            ObjectReference holderRef = getFieldObject(arrayElem, "_holder");
            StringReference classNameRef = (StringReference) getFieldObject(holderRef, "_className");
            ArrayReference pathSpecs = (ArrayReference) getFieldObject(arrayElem, "_pathSpecs");
            for (Value pathSpecsRefRaw : pathSpecs.getValues()) {
                StringReference pathSpec = (StringReference) pathSpecsRefRaw;
                Set<String> collection = this.filterMap.get(contextPathRef.value()).get(classNameRef.value());
                if (collection == null) {
                    this.filterMap.get(contextPathRef.value()).put(classNameRef.value(), new HashSet<>());
                }
                this.filterMap.get(contextPathRef.value()).get(classNameRef.value()).add(pathSpec.value());
            }
        }
    }

    private void handlerStruts2() {
        this.filterMap.forEach((contextPath, value) -> {
            value.forEach((className, urlPatterns) -> {
                if (className.equals("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter")) {
//                    struts2.2/2.3
                    urlPatterns.forEach(urlPattern -> {
                        struts.registryPrefix(urlPattern);
                    });
                }
                if (className.equals("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")) {
//                    struts2.5
                    urlPatterns.forEach(urlPattern -> {
                        struts.registryPrefix(urlPattern);
                    });
                }
            });
        });
    }

    private void handleTomcat98(ObjectReference tomcatObjectRef) {
        ObjectReference containListRef = getFieldObject(tomcatObjectRef, "contextObjectToContextVersionMap");
        Map<ObjectReference, ObjectReference> hashMapObjectRef = MapAnalyse.getConcurrentHashMapObject(containListRef);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObjectRef.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference keyRef = next.getKey();
            StringReference pathRef = (StringReference) getFieldObject(keyRef, "path");
            this.filterMap.put(pathRef.value(), new LinkedHashMap<>());
            ObjectReference filterDefsRef = getFieldObject(keyRef, "filterDefs");
            HashMap<String, String> filterDefHashMap = getFilterDefHashMap(filterDefsRef);
            ObjectReference filterMapsRef = getFieldObject(keyRef, "filterMaps");
            HashMap<String, Set<String>> filterMapHashMap = getFilterMapHashMap(filterMapsRef);
            filterMapHashMap.forEach((key, value) -> {
                String className = filterDefHashMap.get(key);
                Set<String> collection = this.filterMap.get(pathRef.value()).get(className);
                if (collection == null) {
                    this.filterMap.get(pathRef.value()).put(className, new HashSet<>());
                }
                this.filterMap.get(pathRef.value()).get(className).addAll(value);
            });
        }
    }

    /**
     * JDI filterMaps 远程翻译
     * aliasName -> urlPatterns
     * LinkedHashMap : Filter注重顺序
     *
     * @param filterMapsRef
     * @return
     */
    private LinkedHashMap<String, Set<String>> getFilterMapHashMap(ObjectReference filterMapsRef) {
        ArrayReference arrayRef = (ArrayReference) getFieldObject(filterMapsRef, "array");
        LinkedHashMap<String, Set<String>> filterMapResult = new LinkedHashMap<>();
        if (arrayRef == null) {
            return filterMapResult;
        }
        for (Value arrayElemRefRaw : arrayRef.getValues()) {
            ObjectReference arrayElemRef = (ObjectReference) arrayElemRefRaw;
            StringReference filterNameRef = (StringReference) getFieldObject(arrayElemRef, "filterName");
            ArrayReference urlPatterns = (ArrayReference) getFieldObject(arrayElemRef, "urlPatterns");
            if (urlPatterns.getValues().size() > 0) {
                filterMapResult.put(filterNameRef.value(), new HashSet<>());
            }
            for (Value urlPatternRefRaw : urlPatterns.getValues()) {
                StringReference urlPatternRef = (StringReference) urlPatternRefRaw;
                filterMapResult.get(filterNameRef.value()).add(urlPatternRef.value());
            }
        }
        return filterMapResult;
    }

    /**
     * JDI filterDefs 远程翻译
     * k->v
     * FilterAliasName -> className
     *
     * @param filterDefsRef
     * @return
     */
    private HashMap<String, String> getFilterDefHashMap(ObjectReference filterDefsRef) {
        Map<ObjectReference, ObjectReference> hashMapObjectRef = MapAnalyse.getHashMapObject(filterDefsRef);
        HashMap<String, String> filterDefsMapResult = new HashMap<>();
        hashMapObjectRef.forEach((ObjectReference keyRef, ObjectReference valueRef) -> {
            String value = ((StringReference) keyRef).value();
            StringReference filterClassRef = (StringReference) getFieldObject(valueRef, "filterClass");
            filterDefsMapResult.put(value, filterClassRef.value());
        });
        return filterDefsMapResult;
    }

    @Override
    public void setHandleOrPlugin() {
        this.handleOrPlugin = "filter[过滤器]";
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return new HashMap<>();
    }

    public HashMap<String, LinkedHashMap<String, Set<String>>> getFilterMap() {
        return this.filterMap;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public void clearData() {
        super.clearData();
        tomcatOrJettyObjects = new HashSet<>();
        this.filterMap = new HashMap<>();
    }
}
