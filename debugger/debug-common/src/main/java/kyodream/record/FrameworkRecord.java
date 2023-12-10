package kyodream.record;

import kyodream.map.AnalystsType;

import java.util.LinkedHashMap;
import java.util.Map;

public class FrameworkRecord {

    private AnalystsType type;

    private String contextPath;

    private Map<String, String> urlMap;

    private LinkedHashMap<String , String> filterMap;

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AnalystsType getType() {
        return type;
    }

    public void setType(AnalystsType type) {
        this.type = type;
    }

    public Map<String, String> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, String> urlMap) {
        this.urlMap = urlMap;
    }

    public LinkedHashMap<String, String> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(LinkedHashMap<String, String> filterMap) {
        this.filterMap = filterMap;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
