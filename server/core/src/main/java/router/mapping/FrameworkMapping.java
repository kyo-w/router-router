package router.mapping;

import router.type.HandlerType;

import java.util.*;

public class FrameworkMapping {

    private HandlerType type;

    private String contextPath;

    private List<ServletMapping> urlMap;

    private String version;

    private HashMap<String, ServletMapping> __serlvetCache = new HashMap<>();

    public FrameworkMapping() {
        urlMap = new ArrayList<>();
        version = "UNKNOWN";
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version != null) {
            this.version = version;
        }
    }

    public HandlerType getType() {
        return type;
    }

    public void setType(HandlerType type) {
        this.type = type;
    }

    public List<ServletMapping> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(List<ServletMapping> urlMap) {
        this.urlMap = urlMap;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void recordServletMap(String classname, String url) {
        if (urlMap == null) urlMap = new ArrayList<>();
        if (!__serlvetCache.containsKey(classname)) {
            ServletMapping servletMapping = new ServletMapping(classname, new ArrayList<>());
            servletMapping.addPath(url);
            __serlvetCache.put(classname, servletMapping);
            urlMap.add(servletMapping);
        } else {
            __serlvetCache.get(classname).addPath(url);
        }
    }
}
