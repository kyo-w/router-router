package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.sun.jdi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Tomcat Filter
 */
@Component
public class Filter extends DefaultFramework {
    @Autowired
    Struts struts;

    /**
     * k->(s->(v))
     * k: webapp path
     * s: filter ClassName
     * v: urlPatterns
     */
    private HashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> filterMap = new HashMap<>();


    @Override
    public boolean analystsFrameworkObject(VirtualMachine vm) {
        for (ObjectReference tomcatOrJettyObjectRef : this.getAnalystsObject()) {
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
        registryStruts2Prefix();
        if (struts.getPrefix() != null) {
            struts.startAnalysts(vm);
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
                    this.filterMap.get(contextPathRef.value()).put(classNameRef.value(), new LinkedHashSet<>());
                }
                this.filterMap.get(contextPathRef.value()).get(classNameRef.value()).add(pathSpec.value());
            }
        }
    }

    private void registryStruts2Prefix() {
        this.filterMap.forEach((contextPath, value) -> {
            value.forEach((className, urlPatterns) -> {
                if (className.equals("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter")) {
//                    struts2.2/2.3
                    urlPatterns.forEach(urlPattern -> {
                        struts.setStrutsVersion(2);
                        struts.registryPrefix(urlPattern);
                    });
                }
                if (className.equals("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")) {
//                    struts2.5
                    urlPatterns.forEach(urlPattern -> {
                        struts.setStrutsVersion(2);
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
                    this.filterMap.get(pathRef.value()).put(className, new LinkedHashSet<>());
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
            if (urlPatterns.getValues().size() ==  0) {
                continue;
            }
            for (Value urlPatternRefRaw : urlPatterns.getValues()) {
                StringReference urlPatternRef = (StringReference) urlPatternRefRaw;
                Set<String> result = filterMapResult.get(filterNameRef.value());
                if(result == null){
                    result = new LinkedHashSet<>();
                    filterMapResult.put(filterNameRef.value(), result);
                }
                result.add(urlPatternRef.value());
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
    public void setHandleOrFrameworkName() {
        this.handleOrFrameworkName = "Filter";
    }

    public HashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> getFilterMap() {
        return this.filterMap;
    }

    @Override
    public void clearAny() {
        super.clearAny();
        this.filterMap.clear();
    }

}
