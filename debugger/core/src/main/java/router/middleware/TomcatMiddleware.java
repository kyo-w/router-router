package router.middleware;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.StringReference;
import router.analysts.ArrayAnalysts;
import router.analysts.IAnalysts;
import router.analysts.MapAnalysts;
import router.context.Context;
import router.mapping.FilterMapping;
import router.mapping.MiddlewareMapping;
import router.except.MiddleWareError;
import router.handler.BreakPointHandler;
import router.parse.UrlParse;
import router.parse.UrlType;
import router.pipeline.FilterPipeLine;
import router.pipeline.ServletPipeLine;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.MiddlewareType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class TomcatMiddleware implements BreakPointHandler {
    @Override
    public void handlerTarget(IAnalysts thisObject, Context context) throws MiddleWareError {
        try {
            MapAnalysts contextVersionMaps = new MapAnalysts(thisObject.getObjFields("contextObjectToContextVersionMap"));
            MapAnalysts.Entry[] contextKv = contextVersionMaps.getKV();
            context.getPublish().Event(EventType.MiddlewareContextCount,
                    new EventPackage(contextVersionMaps.getId(), contextKv.length));
            for (MapAnalysts.Entry contextRef : contextKv) {
                MiddlewareMapping tomcat = new MiddlewareMapping();
                tomcat.setType(MiddlewareType.Tomcat);
                IAnalysts standardContextRef = contextRef.getKey();
                IAnalysts mapperRef = contextRef.getValue();
                // set VirtualPath / rootPath
                setContextPath(tomcat, standardContextRef);
                setVersion(thisObject, tomcat);
                // setFilter
                analystsFilter(standardContextRef, tomcat, context);
                // set Servlet
                analystsMapper(mapperRef, tomcat, context);
                context.pushMiddleware(tomcat);
                context.getPublish().Event(EventType.MiddlewareContextAnalystsComplete,
                        new EventPackage(contextVersionMaps.getId(), contextRef.toString()));
            }
        } catch (Exception e) {
            throw MiddleWareError.TomcatError(e.getMessage());
        }
    }

    private void setVersion(IAnalysts thisObject, MiddlewareMapping tomcatRecord) {
        String packageVersion = thisObject.getPackageVersion(thisObject.getClassName());
        tomcatRecord.setVersion(packageVersion);
    }

    private void setContextPath(MiddlewareMapping tomcatRecord, IAnalysts standardContextRef) throws Exception {
        String docBase = standardContextRef.getStrFields("docBase");
        String path = standardContextRef.getStrFields("path");
        tomcatRecord.setPhysicalPath(docBase);
        tomcatRecord.setVirtualPath(path);
    }

    private void analystsMapper(IAnalysts analystsMapperRef, MiddlewareMapping middlewareMapping, Context context) throws Exception {
        IAnalysts classLoader = analystsMapperRef.getObjFields("object", "loader", "classLoader");
        IAnalysts defaultWrapperRef = analystsMapperRef.getObjFields("defaultWrapper");
        IAnalysts exactWrappersRef = analystsMapperRef.getObjFields("exactWrappers");
        IAnalysts wildcardWrappersRef = analystsMapperRef.getObjFields("wildcardWrappers");
        IAnalysts extensionWrappersRef = analystsMapperRef.getObjFields("extensionWrappers");
        recordMapper(UrlType.DEFAULT, defaultWrapperRef, classLoader, middlewareMapping, context);
        recordMapper(UrlType.EXACT, exactWrappersRef, classLoader, middlewareMapping, context);
        recordMapper(UrlType.WILD, wildcardWrappersRef, classLoader, middlewareMapping, context);
        recordMapper(UrlType.EXT, extensionWrappersRef, classLoader, middlewareMapping, context);
    }

    private void recordMapper(UrlType urlType, IAnalysts wrappersRef, IAnalysts classLoader, MiddlewareMapping middlewareMapping, Context context) throws Exception {
        if (wrappersRef.getRawValue() instanceof ArrayReference) {
            ArrayAnalysts mappersRef = new ArrayAnalysts(wrappersRef);
            context.getPublish().Event(EventType.ServletCount, new EventPackage(mappersRef.getId(), mappersRef.size()));
            for (IAnalysts mapperRef : mappersRef) {
                String classname = setServletMap(urlType, mapperRef, classLoader, middlewareMapping, context);
                context.getPublish().Event(EventType.ServletAnalystsComplete, new EventPackage(mapperRef.getId(), classname));
            }
        } else {
            context.getPublish().Event(EventType.ServletCount, new EventPackage(wrappersRef.getId(), 1));
            String classname = setServletMap(urlType, wrappersRef, classLoader, middlewareMapping, context);
            context.getPublish().Event(EventType.ServletAnalystsComplete, new EventPackage(wrappersRef.getId(), classname));

        }
    }


    private String setServletMap(UrlType urlType, IAnalysts mapperRef, IAnalysts classLoader, MiddlewareMapping tomcat, Context context) throws Exception {
        String subUrlPath = mapperRef.getObjFields("name").convertToString();
        IAnalysts standardWrapperRef = mapperRef.getObjFields("object");
        IAnalysts instanceRef = standardWrapperRef.getObjFields("instance");
        String servletClass = standardWrapperRef.getStrFields("servletClass");
        UrlParse urlParse = new UrlParse(urlType, tomcat.getVirtualPath(), subUrlPath);
        if (!instanceRef.isObjRef()) {
            instanceRef = ServletPipeLine.initServlet(MiddlewareType.Tomcat, standardWrapperRef, classLoader);
        }
        if (instanceRef != null) {
            IAnalysts attributes = standardWrapperRef.getObjFields("facade", "context", "context", "attributes");
            ServletPipeLine.handlerServlet(urlParse, instanceRef, attributes, context);
        } else {
            throw new Exception(servletClass + "初始化失败，回滚状态");
        }
        tomcat.recordServletMap(servletClass, urlParse.getPath());
        return servletClass;
    }

    private void analystsFilter(IAnalysts standardContextRef, MiddlewareMapping tomcat, Context context) throws Exception {
        IAnalysts filterList = standardContextRef.getObjFields("filterMaps", "array");
        MapAnalysts.Entry[] mapInstance = new MapAnalysts(standardContextRef.getObjFields("filterConfigs")).getKV();
        ArrayAnalysts filtersRef = new ArrayAnalysts(filterList);
        context.getPublish().Event(EventType.FilterCount, new EventPackage(filtersRef.getId(), filtersRef.size()));
        for (IAnalysts filterRef : filtersRef) {
            String filterName = filterRef.getStrFields("filterName");
            MapAnalysts.Entry mapRef = Arrays.stream(mapInstance).filter(e -> {
                try {
                    return e.getKey().convertToString().equals(filterName);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).findFirst().get();
            IAnalysts filterObject = mapRef.getValue().getObjFields("filter");
            String className = filterObject.getClassName();
            ArrayAnalysts urlPatternsArrayRef = new ArrayAnalysts(filterRef.getObjFields("urlPatterns"));
            for (IAnalysts urlPatternsRef : urlPatternsArrayRef) {
                if (urlPatternsRef.getRawValue() instanceof StringReference) {
                    String subPath = urlPatternsRef.convertToString();
                    FilterPipeLine.doFilter(UrlParse.getMiddlewareParse(tomcat.getVirtualPath(), subPath), filterObject, context);
                    String path = UrlParse.concatSubPath(tomcat.getVirtualPath(), subPath);
                    tomcat.recordFilterMap(new FilterMapping(className, path));
                    context.getPublish().Event(EventType.FilterAnalystsComplete, new EventPackage(filtersRef.getId(), className));
                }
            }
        }
    }


    @Override
    public MiddlewareType getHandlerName() {
        return MiddlewareType.Tomcat;
    }
}
