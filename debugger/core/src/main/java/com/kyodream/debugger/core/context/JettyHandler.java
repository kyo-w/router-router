package com.kyodream.debugger.core.context;

import kyodream.analysts.ArrayAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.map.AnalystsType;
import kyodream.record.ContextRecord;
import kyodream.record.ContextUrlRecord;
import kyodream.record.FilterRecord;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class JettyHandler extends ContextBreakPointHandler {
    private JettyHandler() {
    }

    @Getter
    private static JettyHandler instance = new JettyHandler();

    @Override
    public CONTEXT_TYPE getContextType() {
        return CONTEXT_TYPE.JETTY;
    }

    @Override
    public void handlerTarget(ObjectAnalysts thisObject) {
        System.out.println(thisObject.className());
        ContextRecord jettyRecord = new ContextRecord();
        jettyRecord.setType(AnalystsType.JETTY_WEB);

//        set virtual path / root path
        setContextPath(jettyRecord, thisObject);
        setVersion(thisObject, jettyRecord);
        ObjectAnalysts servletHandlerRef = thisObject.getFieldsRef("_servletHandler");
        setServletMapping(servletHandlerRef, jettyRecord);
        setFilterMapping(servletHandlerRef, jettyRecord);
        thisObject.contextMsg(jettyRecord);
    }

    private void setVersion(ObjectAnalysts thisObject, ContextRecord jettyRecord) {
        String packageVersion = thisObject.getPackageVersion(thisObject.className());
        jettyRecord.setVersion(packageVersion);
    }

    private void setFilterMapping(ObjectAnalysts servletHandlerRef, ContextRecord jettyRecord) {
        LinkedList<FilterRecord> result = new LinkedList<>();
        ArrayAnalysts filterArrayRef = new ArrayAnalysts(servletHandlerRef.getFieldsRef("_filterMappings"));
        for (ObjectAnalysts filterMapRef : filterArrayRef) {
            String className = filterMapRef.getFieldsRef("_holder", "_className").getString();
            ArrayAnalysts pathsRef = new ArrayAnalysts(filterMapRef.getFieldsRef("_pathSpecs"));
            for (ObjectAnalysts pathRef : pathsRef) {
                String path = pathRef.getString();
                ObjectAnalysts instance = filterMapRef.getFieldsRef("_holder", "_filter");
                ContextUrlRecord contextUrlRecord = ContextUrlRecord.getContextUrlRecord(jettyRecord.getVirtualPath(), path);
                handlerFilterInstance(contextUrlRecord, instance);
                result.add(new FilterRecord(className, ContextUrlRecord.concatSubPath(jettyRecord.getVirtualPath(), path)));
            }
        }
        jettyRecord.setFilterMap(result);
    }

    private void setContextPath(ContextRecord jettyRecord, ObjectAnalysts thisObject) {
        String path = thisObject.getStringField("_contextPath");
        jettyRecord.setVirtualPath(path);
        ObjectAnalysts baseResourceRef = thisObject.getFieldsRef("_baseResource");
        if (baseResourceRef.className().equals("org.eclipse.jetty.util.resource.PathResource")) {
            String rootPath = baseResourceRef.getFieldsRef("path", "path").getString();
            jettyRecord.setPhysicalPath(rootPath);
        }
    }

    private void setServletMapping(ObjectAnalysts servletHandlerRef, ContextRecord contextRecord) {
        HashMap<String, String> result = new HashMap<>();
        ArrayAnalysts servletsRef = new ArrayAnalysts(servletHandlerRef.getFieldsRef("_servlets"));
        HashMap<ContextUrlRecord, String> servletMappings = getAliasHashMap(contextRecord, servletHandlerRef.getFieldsRef("_servletMappings"));
        for (ObjectAnalysts servletHolderRef : servletsRef) {
            String alias = servletHolderRef.getStringField("_name");
            String className = servletHolderRef.getStringField("_className");
            ContextUrlRecord[] contextUrlRecords = servletMappings.entrySet().stream().filter(elem -> elem.getValue().equals(alias)).map(Map.Entry::getKey).toArray(ContextUrlRecord[]::new);
            ObjectAnalysts servletWrapperRef = servletHolderRef.getFieldsRef("_servlet");
            if (servletWrapperRef.isEmpty()) {
                initServlet(servletHolderRef, null);
                servletWrapperRef = servletHolderRef.getFieldsRef("_servlet");
            }
            for (ContextUrlRecord urlRecord : contextUrlRecords) {
                if (!servletWrapperRef.isEmpty()) {
                    ObjectAnalysts instance = servletWrapperRef.getFieldsRef("_wrappedServlet");
                    ObjectAnalysts attributesRef = servletHandlerRef.getFieldsRef("_servletContext", "_map", "value");
                    handlerServlet(urlRecord, instance, attributesRef);
                }
                result.put(urlRecord.getPattern(), className);
            }
        }
        contextRecord.setServletMap(result);
    }

    private HashMap<ContextUrlRecord, String> getAliasHashMap(ContextRecord contextRecord, ObjectAnalysts servletMappingsRef) {
        HashMap<ContextUrlRecord, String> result = new HashMap<>();
        ArrayAnalysts servletsMapRef = new ArrayAnalysts(servletMappingsRef);
        for (ObjectAnalysts servletMapRef : servletsMapRef) {
            ObjectAnalysts pathSpecsRef = servletMapRef.getFieldsRef("_pathSpecs");
            String name = servletMapRef.getStringField("_servletName");
            ArrayAnalysts pathsRef = new ArrayAnalysts(pathSpecsRef);
            for (ObjectAnalysts pathRef : pathsRef) {
                String path = pathRef.getString();
                ContextUrlRecord contextUrlRecord = ContextUrlRecord.getContextUrlRecord(contextRecord.getVirtualPath(), path);
                result.put(contextUrlRecord, name);
            }
        }
        return result;
    }
}
