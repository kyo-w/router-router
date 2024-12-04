package router.middleware;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.StringReference;
import router.analysts.ArrayAnalysts;
import router.analysts.IAnalysts;
import router.analysts.MapAnalysts;
import router.context.Context;
import router.except.FieldExcept;
import router.mapping.FilterMapping;
import router.mapping.MiddlewareMapping;
import router.except.MiddleWareError;
import router.handler.BreakPointHandler;
import router.parse.UrlParse;
import router.parse.UrlType;
import router.pipeline.FilterPipeLine;
import router.pipeline.ServletPipeLine;
import router.publish.*;
import router.publish.Error;
import router.type.MiddlewareType;

import java.util.Arrays;

public class TomcatMiddleware implements BreakPointHandler {
    @Override
    public void handlerTarget(IAnalysts thisObject, Context context) throws MiddleWareError {
        MapAnalysts contextVersionMaps = null;
        try {
            contextVersionMaps = new MapAnalysts(thisObject.getObjFields("contextObjectToContextVersionMap"));
        } catch (FieldExcept e) {
            throw new MiddleWareError(e.getMessage());
        }
        MapAnalysts.Entry[] contextKv = contextVersionMaps.getKV();
        context.getPublish().Event(new StartEvent(EventType.MiddlewareContextCount,
                contextVersionMaps.getId(), getHandlerName().name(), contextKv.length));
        for (MapAnalysts.Entry contextRef : contextKv) {
            MiddlewareMapping tomcat = new MiddlewareMapping();
            tomcat.setType(MiddlewareType.Tomcat);
            try {
                IAnalysts standardContextRef = contextRef.getKey();
                IAnalysts mapperRef = contextRef.getValue();
                setContextPath(tomcat, standardContextRef);
                setVersion(thisObject, tomcat);
                analystsFilter(standardContextRef, tomcat, context);
                analystsMapper(mapperRef, tomcat, context);
                context.pushMiddleware(tomcat);
                context.getPublish().Event(
                        new EndEvent(EventType.MiddlewareContextAnalystsComplete,
                                contextVersionMaps.getId(), "tomcat完成分析"));
            } catch (MiddleWareError e) {
                context.getPublish().Error(
                        new ErrorEvent(ErrorType.MiddlewareError, contextVersionMaps.getId(), e));
            }
        }
    }

    private void setVersion(IAnalysts thisObject, MiddlewareMapping tomcatRecord) {
        String packageVersion = thisObject.getPackageVersion(thisObject.getClassName());
        tomcatRecord.setVersion(packageVersion);
    }

    private void setContextPath(MiddlewareMapping tomcatRecord, IAnalysts standardContextRef) throws MiddleWareError {
        try {
            String docBase = standardContextRef.getStrFields("docBase");
            String path = standardContextRef.getStrFields("path");
            tomcatRecord.setPhysicalPath(docBase);
            tomcatRecord.setVirtualPath(path);
        } catch (Exception e) {
            throw new MiddleWareError("无法获取物理/虚拟路径");
        }
    }

    private void analystsMapper(IAnalysts analystsMapperRef, MiddlewareMapping middlewareMapping, Context context) throws MiddleWareError {
        IAnalysts defaultWrapperRef = null;
        IAnalysts classLoader = null;
        IAnalysts exactWrappersRef = null;
        IAnalysts wildcardWrappersRef = null;
        IAnalysts extensionWrappersRef = null;
        try {
            classLoader = analystsMapperRef.getObjFields("object", "loader", "classLoader");
            defaultWrapperRef = analystsMapperRef.getObjFields("defaultWrapper");
            exactWrappersRef = analystsMapperRef.getObjFields("exactWrappers");
            wildcardWrappersRef = analystsMapperRef.getObjFields("wildcardWrappers");
            extensionWrappersRef = analystsMapperRef.getObjFields("extensionWrappers");
        } catch (Exception e) {
            throw new MiddleWareError("无法获取servlet上下文");
        }
//        recordMapper(UrlType.DEFAULT, defaultWrapperRef, classLoader, middlewareMapping, context);
//        recordMapper(UrlType.EXACT, exactWrappersRef, classLoader, middlewareMapping, context);
        recordMapper(UrlType.WILD, wildcardWrappersRef, classLoader, middlewareMapping, context);
        recordMapper(UrlType.EXT, extensionWrappersRef, classLoader, middlewareMapping, context);
    }

    private void recordMapper(UrlType urlType, IAnalysts wrappersRef, IAnalysts classLoader, MiddlewareMapping middlewareMapping, Context context) {
        if (wrappersRef.getRawValue() instanceof ArrayReference) {
            ArrayAnalysts mappersRef = new ArrayAnalysts(wrappersRef);
            context.getPublish().Event(new StartEvent(EventType.ServletCount,
                    mappersRef.getId(), mappersRef.getClassName(), mappersRef.size()));

            try {
                for (IAnalysts mapperRef : mappersRef) {
                    String classname = setServletMap(urlType, mapperRef, classLoader, middlewareMapping, context);
                    context.getPublish().Event(new EndEvent(EventType.ServletAnalystsComplete, mappersRef.getId(), classname));
                }
            } catch (Exception e) {
                context.getPublish().Error(new ErrorEvent(ErrorType.ServletError, mappersRef.getId(), e));
            }
        } else {
            context.getPublish().Event(new StartEvent(EventType.ServletCount, wrappersRef.getId(), wrappersRef.getClassName(), 1));
            try {
                String classname = setServletMap(urlType, wrappersRef, classLoader, middlewareMapping, context);
                context.getPublish().Event(new EndEvent(EventType.ServletAnalystsComplete, wrappersRef.getId(), classname));
            } catch (Exception e) {
                context.getPublish().Error(new ErrorEvent(ErrorType.ServletError, wrappersRef.getId(), e));
            }
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
            System.out.println(servletClass + "初始化失败");
        }
        tomcat.recordServletMap(servletClass, urlParse.getPath());
        return servletClass;
    }

    private void analystsFilter(IAnalysts standardContextRef, MiddlewareMapping tomcat, Context context) throws MiddleWareError {
        IAnalysts filterList = null;
        MapAnalysts.Entry[] mapInstance = null;
        try {
            filterList = standardContextRef.getObjFields("filterMaps", "array");
            mapInstance = new MapAnalysts(standardContextRef.getObjFields("filterConfigs")).getKV();
        } catch (Exception error) {
            throw new MiddleWareError("无法获取filter上下文");
        }
        ArrayAnalysts filtersRef = new ArrayAnalysts(filterList);
        context.getPublish().Event(new StartEvent(EventType.FilterCount, filtersRef.getId(), filtersRef.getClassName(), filtersRef.size()));
        try {
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
                    }
                }
                context.getPublish().Event(new EndEvent(EventType.FilterAnalystsComplete, filtersRef.getId(), className));
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.FilterError, filtersRef.getId(), e));
        }
    }


    @Override
    public MiddlewareType getHandlerName() {
        return MiddlewareType.Tomcat;
    }
}
