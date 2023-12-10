package java.kyodream.record;

import java.kyodream.utils.VirtualPathUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下文内容：
 * 物理根目录路径: rootPath
 * 网络虚拟根路径: virtualPath
 * 路径映射关系: urlMap
 * 过滤器映射关系: filterMap
 * JSP路径: jspMap
 */
public class Context {

    private String from;

    private String rootPath;
    private String virtualPath;
    private HashMap<String, String> urlMap;
    private FilterMap[] filterMap;
    private HashMap<String, String> jspMap;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public HashMap<String, String> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(HashMap<String, String> urlMap) {
        this.urlMap = urlMap;
    }

    public HashMap<String, String> getJspMap() {
        return jspMap;
    }

    public void setJspMap(HashMap<String, String> jspMap) {
        this.jspMap = jspMap;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public FilterMap[] getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(FilterMap[] filterMap) {
        this.filterMap = filterMap;
    }

    public static class FilterMap {
        private String url;
        private String className;

        public FilterMap(String url, String className) {
            this.url = url;
            this.className = className;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class urlMap {
        private String url;
        private String className;

        public urlMap(String url, String className) {
            this.url = url;
            this.className = className;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
