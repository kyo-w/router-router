package com.kyodream.debugger.core.context;

import com.sun.jdi.*;
import kyodream.analysts.ArrayAnalysts;
import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.map.AnalystsType;
import kyodream.record.ContextRecord;
import kyodream.record.ContextUrlRecord;
import kyodream.record.FilterRecord;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class TomcatHandler extends ContextBreakPointHandler {
    private TomcatHandler() {
    }

    @Getter
    private static final TomcatHandler instance = new TomcatHandler();

    @Override
    public CONTEXT_TYPE getContextType() {
        return CONTEXT_TYPE.TOMCAT;
    }

    @Override
    public void handlerTarget(ObjectAnalysts thisObject) {
//        tomcatRecord.
        MapAnalysts.Entry[] contextObjectToContextVersionMaps = new MapAnalysts(thisObject.getFieldsRef("contextObjectToContextVersionMap")).getKV();
        for (MapAnalysts.Entry contextRef : contextObjectToContextVersionMaps) {
            ContextRecord tomcatRecord = new ContextRecord();
            tomcatRecord.setType(AnalystsType.TOMCAT);
            ObjectAnalysts standardContextRef = contextRef.getKey();
            ObjectAnalysts mapperRef = contextRef.getValue();
            // set VirtualPath / rootPath
            setContextPath(tomcatRecord, standardContextRef);
            setVersion(thisObject, tomcatRecord);
            // setFilter
            analystsStandardContext(standardContextRef, tomcatRecord);
            // set Servlet
            analystsMapper(mapperRef, tomcatRecord);
            thisObject.contextMsg(tomcatRecord);
        }
    }

    private void setVersion(ObjectAnalysts thisObject, ContextRecord tomcatRecord) {
        String packageVersion = thisObject.getPackageVersion(thisObject.className());
        tomcatRecord.setVersion(packageVersion);
    }

    private void setContextPath(ContextRecord tomcatRecord, ObjectAnalysts standardContextRef) {
        String docBase = standardContextRef.getStringField("docBase");
        String path = standardContextRef.getStringField("path");
        tomcatRecord.setPhysicalPath(docBase);
        tomcatRecord.setVirtualPath(path);
    }

    private void analystsMapper(ObjectAnalysts analystsMapperRef, ContextRecord contextRecord) {
        ObjectAnalysts classLoader = analystsMapperRef.getFieldsRef("object", "loader", "classLoader");
        ObjectAnalysts defaultWrapperRef = analystsMapperRef.getFieldsRef("defaultWrapper");
        ObjectAnalysts exactWrappersRef = analystsMapperRef.getFieldsRef("exactWrappers");
        ObjectAnalysts wildcardWrappersRef = analystsMapperRef.getFieldsRef("wildcardWrappers");
        ObjectAnalysts extensionWrappersRef = analystsMapperRef.getFieldsRef("extensionWrappers");
        contextRecord.setServletMap(new HashMap<>());
        recordMapper(ContextUrlRecord.UrlType.DEFAULT, defaultWrapperRef, classLoader, contextRecord);
        recordMapper(ContextUrlRecord.UrlType.EXACT, exactWrappersRef, classLoader, contextRecord);
        recordMapper(ContextUrlRecord.UrlType.WILD, wildcardWrappersRef, classLoader, contextRecord);
        recordMapper(ContextUrlRecord.UrlType.EXT, extensionWrappersRef, classLoader, contextRecord);
    }

    private void recordMapper(ContextUrlRecord.UrlType urlType, ObjectAnalysts wrappersRef, ObjectAnalysts classLoader, ContextRecord contextRecord) {
        if (wrappersRef.getRef() instanceof ArrayReference) {
            ArrayAnalysts mappersRef = new ArrayAnalysts(wrappersRef);
            for (ObjectAnalysts mapperRef : mappersRef) {
                setServletMap(urlType, mapperRef, classLoader, contextRecord);
            }
        } else {
            setServletMap(urlType, wrappersRef, classLoader, contextRecord);
        }
    }

    private void setServletMap(ContextUrlRecord.UrlType urlType, ObjectAnalysts mapperRef, ObjectAnalysts classLoader, ContextRecord contextRecord) {
        String subUrlPath = mapperRef.getFieldsRef("name").getString();
        ObjectAnalysts standardWrapperRef = mapperRef.getFieldsRef("object");
        ObjectAnalysts instanceRef = standardWrapperRef.getFieldsRef("instance");
        String servletClass = standardWrapperRef.getStringField("servletClass");
        ContextUrlRecord urlRecord = new ContextUrlRecord(urlType, contextRecord.getVirtualPath(), subUrlPath);
        if (instanceRef.isEmpty()) {
            System.out.println(urlRecord.getPattern());
            System.out.println(servletClass + "进行初始化");
            instanceRef = initServlet(standardWrapperRef, classLoader);
        }
        if (!instanceRef.isEmpty()) {
            ObjectAnalysts attributes = standardWrapperRef.getFieldsRef("facade", "context", "context", "attributes");
            handlerServlet(urlRecord, instanceRef, attributes);
        } else {
            System.out.println(servletClass + "初始化失败，回滚状态");
        }
        contextRecord.getServletMap().put(urlRecord.getPattern(), servletClass);
    }


    private void analystsStandardContext(ObjectAnalysts standardContextRef, ContextRecord contextRecord) {
        ObjectAnalysts analystsObject = standardContextRef.getFieldsRef("filterMaps", "array");
        MapAnalysts.Entry[] mapInstance = new MapAnalysts(standardContextRef.getFieldsRef("filterConfigs")).getKV();
        LinkedList<FilterRecord> filter = getFilter(mapInstance, analystsObject, contextRecord);
        contextRecord.setFilterMap(filter);
    }

    private LinkedList<FilterRecord> getFilter(MapAnalysts.Entry[] mapInstance, ObjectAnalysts filterList, ContextRecord contextRecord) {
        LinkedList<FilterRecord> filterResult = new LinkedList<>();
        ArrayAnalysts filtersRef = new ArrayAnalysts(filterList);
        for (ObjectAnalysts filterRef : filtersRef) {
            String filterName = filterRef.getStringField("filterName");
            MapAnalysts.Entry mapRef = Arrays.stream(mapInstance).filter(e -> e.getKey().getString().equals(filterName)).findFirst().get();
            ObjectAnalysts filterObject = mapRef.getValue().getFieldsRef("filter");
            String className = filterObject.className();
            ArrayAnalysts urlPatternsArrayRef = new ArrayAnalysts(filterRef.getFieldsRef("urlPatterns"));
            for (ObjectAnalysts urlPatternsRef : urlPatternsArrayRef) {
                if (urlPatternsRef.getRef() instanceof StringReference) {
                    String subPath = urlPatternsRef.getString();
                    handlerFilterInstance(ContextUrlRecord.getContextUrlRecord(contextRecord.getVirtualPath(), subPath), filterObject);
                    String path = ContextUrlRecord.concatSubPath(contextRecord.getVirtualPath(), subPath);
                    filterResult.add(new FilterRecord(className, path));
                }
            }
        }
        return filterResult;
    }
}
