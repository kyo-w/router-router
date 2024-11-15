package router.context;

import router.analysts.IAnalysts;
import router.mapping.FilterMapping;
import router.mapping.FrameworkMapping;
import router.mapping.MiddlewareMapping;
import router.mapping.ServletMapping;
import router.publish.IPublish;
import router.type.HandlerType;
import router.type.MiddlewareType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CacheContext implements Context {
    public List<MiddlewareMapping> middlewareCache;
    public List<FrameworkMapping> frameworkCache;
    public HashMap<String, ServletMapping> servletCache;
    public LinkedList<FilterMapping> filterCache;
    public HashMap<Long, IAnalysts> analystCache;
    public IPublish publish;

    public static CacheContext build(IPublish publish) {
        CacheContext cacheContext = new CacheContext();
        cacheContext.filterCache = new LinkedList<>();
        cacheContext.middlewareCache = new ArrayList<>();
        cacheContext.frameworkCache = new ArrayList<>();
        cacheContext.servletCache = new HashMap<>();
        cacheContext.analystCache = new HashMap<>();
        cacheContext.publish = publish;
        return cacheContext;
    }


    @Override
    public void pushMiddleware(MiddlewareMapping middleware) {
        middlewareCache.add(middleware);
    }

    @Override
    public void pushFramework(FrameworkMapping framework) {
        frameworkCache.add(framework);
    }

    @Override
    public void completeTask(){}

    @Override
    public IPublish getPublish() {
        return publish;
    }

    @Override
    public void pushAnalysts(IAnalysts analysts) {
        analystCache.put(analysts.getId(), analysts);
    }

    @Override
    public IAnalysts getAnalystsByUniqId(Long id) {
        return analystCache.get(id);
    }
}
