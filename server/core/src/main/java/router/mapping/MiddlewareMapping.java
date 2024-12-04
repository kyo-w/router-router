package router.mapping;

import router.type.MiddlewareType;

import java.util.*;

public class MiddlewareMapping {

    private MiddlewareType type;

    /**
     * virtual path
     * eg: http://127.0.0.1:8080/
     * <p>
     * virtual path is "/"
     */
    private String virtualPath;

    /**
     * The real path of webroot in the operating system
     */
    private String physicalPath;

    private String version;


    private List<ServletMapping> servletMap;


    private List<FilterMapping> filtersMap;

    private HashMap<String, ServletMapping> __serlvetCache = new HashMap<>();
    private static Integer __filterPos = 0;

    public MiddlewareMapping() {
        version = "UNKNOWN";
        servletMap = new ArrayList<>();
        filtersMap = new ArrayList<>();
    }


    public MiddlewareType getType() {
        return type;
    }

    public void setType(MiddlewareType type) {
        this.type = type;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public String getPhysicalPath() {
        return physicalPath;
    }

    public void setPhysicalPath(String physicalPath) {
        this.physicalPath = physicalPath;
    }

    public List<ServletMapping> getServletMap() {
        return servletMap;
    }

    public void coverAllServlet(List<ServletMapping> servletMap) {
        this.servletMap = servletMap;
    }

    public List<FilterMapping> getFilterMap() {
        return filtersMap;
    }

    public void recordFilterMap(FilterMapping filter) {
        if (filtersMap == null) filtersMap = new ArrayList<FilterMapping>();
        filter.setPriority(__filterPos);
        __filterPos++;
        filtersMap.add(filter);
    }

    public void coverAllFilter(List<FilterMapping> filterMappings) {
        filtersMap = filterMappings;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version != null) {
            this.version = version;
        }
    }


    public void recordServletMap(String classname, String url) {
        if (servletMap == null) servletMap = new ArrayList<>();
        if (!__serlvetCache.containsKey(classname)) {
            ServletMapping servletMapping = new ServletMapping(classname, new ArrayList<>());
            servletMapping.addPath(url);
            __serlvetCache.put(classname, servletMapping);
            servletMap.add(servletMapping);
        } else {
            __serlvetCache.get(classname).addPath(url);
        }
    }
}
