package router.middleware;

import router.analysts.ArrayAnalysts;
import router.analysts.IAnalysts;
import router.context.Context;
import router.mapping.FilterMapping;
import router.mapping.MiddlewareMapping;
import router.except.MiddleWareError;
import router.handler.BreakPointHandler;
import router.parse.UrlParse;
import router.pipeline.FilterPipeLine;
import router.pipeline.ServletPipeLine;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.MiddlewareType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class JettyMiddleware implements BreakPointHandler {
    @Override
    public void handlerTarget(IAnalysts thisObject, Context context) throws MiddleWareError {
        try {
            MiddlewareMapping jetty = new MiddlewareMapping();
            jetty.setType(MiddlewareType.Jetty);
//        set virtual path / root path
            setContextPath(jetty, thisObject);
            setVersion(thisObject, jetty);
            IAnalysts servletHandlerRef = thisObject.getObjFields("_servletHandler");
            context.getPublish().Event(EventType.MiddlewareContextCount, new EventPackage(servletHandlerRef.getId(), 1));

            setServletMapping(servletHandlerRef, jetty, context);
            setFilterMapping(servletHandlerRef, jetty, context);
            context.pushMiddleware(jetty);
            context.getPublish().Event(EventType.MiddlewareContextAnalystsComplete,
                    new EventPackage(servletHandlerRef.getId(), servletHandlerRef.toString()));
        } catch (Exception e) {
            throw MiddleWareError.JettyError(e.getMessage());
        }
    }

    private void setVersion(IAnalysts thisObject, MiddlewareMapping jettyRecord) {
        String packageVersion = thisObject.getPackageVersion(thisObject.getClassName());
        jettyRecord.setVersion(packageVersion);
    }

    private void setFilterMapping(IAnalysts servletHandlerRef, MiddlewareMapping jetty, Context context) throws Exception {
        ArrayAnalysts filterArrayRef = new ArrayAnalysts(servletHandlerRef.getObjFields("_filterMappings"));
        context.getPublish().Event(EventType.FilterCount, new EventPackage(filterArrayRef.getId(), filterArrayRef.size()));
        for (IAnalysts filterMapRef : filterArrayRef) {
            String className = filterMapRef.getObjFields("_holder", "_className").convertToString();
            ArrayAnalysts pathsRef = new ArrayAnalysts(filterMapRef.getObjFields("_pathSpecs"));
            for (IAnalysts pathRef : pathsRef) {
                String path = pathRef.convertToString();
                IAnalysts instance = filterMapRef.getObjFields("_holder", "_filter");
                UrlParse urlParse = UrlParse.getMiddlewareParse(jetty.getVirtualPath(), path);
                FilterPipeLine.doFilter(urlParse, instance, context);
                jetty.recordFilterMap(new FilterMapping(className, UrlParse.concatSubPath(jetty.getVirtualPath(), path)));
                context.getPublish().Event(EventType.FilterAnalystsComplete, new EventPackage(filterArrayRef.getId(), className));
            }
        }
    }

    private void setContextPath(MiddlewareMapping jettyRecord, IAnalysts thisObject) throws Exception {
        String path = thisObject.getStrFields("_contextPath");
        jettyRecord.setVirtualPath(path);
        IAnalysts baseResourceRef = thisObject.getObjFields("_baseResource");
        if (baseResourceRef.getClassName().equals("org.eclipse.jetty.util.resource.PathResource")) {
            String rootPath = baseResourceRef.getObjFields("path", "path").convertToString();
            jettyRecord.setPhysicalPath(rootPath);
        }
    }

    private void setServletMapping(IAnalysts servletHandlerRef, MiddlewareMapping jetty, Context context) throws Exception {
        ArrayAnalysts servletsRef = new ArrayAnalysts(servletHandlerRef.getObjFields("_servlets"));
        HashMap<UrlParse, String> servletMappings = getAliasHashMap(jetty, servletHandlerRef.getObjFields("_servletMappings"));
        context.getPublish().Event(EventType.ServletCount, new EventPackage(servletsRef.getId(), servletsRef.size()));
        for (IAnalysts servletHolderRef : servletsRef) {
            String alias = servletHolderRef.getStrFields("_name");
            String className = servletHolderRef.getStrFields("_className");
            IAnalysts servletWrapperRef = servletHolderRef.getObjFields("_servlet");
            if (servletWrapperRef.isObjRef()) {
                ServletPipeLine.initServlet(MiddlewareType.Jetty, servletHolderRef, null);
                servletWrapperRef = servletHolderRef.getObjFields("_servlet");
            }
            UrlParse[] urlParses = servletMappings.entrySet().stream().filter(elem -> elem.getValue().equals(alias)).map(Map.Entry::getKey).toArray(UrlParse[]::new);
            for (UrlParse urlParse : urlParses) {
                if (!servletWrapperRef.isObjRef()) {
                    IAnalysts wrappedServletRef = servletWrapperRef.getObjFields("_wrappedServlet");
                    IAnalysts attributesRef = servletHandlerRef.getObjFields("_servletContext", "_map", "value");
                    ServletPipeLine.handlerServlet(urlParse, wrappedServletRef, attributesRef, context);
                }
                jetty.recordServletMap(className, urlParse.getPath());
                context.getPublish().Event(EventType.ServletAnalystsComplete, new EventPackage(servletsRef.getId(), className));
            }
        }
    }


    private HashMap<UrlParse, String> getAliasHashMap(MiddlewareMapping middlewareMapping, IAnalysts servletMappingsRef) throws Exception {
        HashMap<UrlParse, String> result = new HashMap<>();
        ArrayAnalysts servletsMapRef = new ArrayAnalysts(servletMappingsRef);
        for (IAnalysts servletMapRef : servletsMapRef) {
            IAnalysts pathSpecsRef = servletMapRef.getObjFields("_pathSpecs");
            String name = servletMapRef.getStrFields("_servletName");
            ArrayAnalysts pathsRef = new ArrayAnalysts(pathSpecsRef);
            for (IAnalysts pathRef : pathsRef) {
                String path = pathRef.convertToString();
                UrlParse urlRecord = UrlParse.getMiddlewareParse(middlewareMapping.getVirtualPath(), path);
                result.put(urlRecord, name);
            }
        }
        return result;
    }

    @Override
    public MiddlewareType getHandlerName() {
        return MiddlewareType.Jetty;
    }
}
