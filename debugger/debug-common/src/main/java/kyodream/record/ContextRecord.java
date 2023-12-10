package kyodream.record;

import kyodream.map.AnalystsType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ContextRecord {

    /**
     * id
     */
    private AnalystsType type;

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


    private Map<String, String> servletMap;


    private LinkedList<FilterRecord> filterMap;

    public AnalystsType getType() {
        return type;
    }

    public void setType(AnalystsType type) {
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

    public Map<String, String> getServletMap() {
        return servletMap;
    }

    public void setServletMap(Map<String, String> servletMap) {
        this.servletMap = servletMap;
    }

    public LinkedList<FilterRecord> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(LinkedList<FilterRecord> filterMap) {
        this.filterMap = filterMap;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
