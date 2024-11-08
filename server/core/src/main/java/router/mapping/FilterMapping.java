package router.mapping;

public class FilterMapping {
    private Integer priority;
    private String className;
    private String urlPath;

    public FilterMapping(String className, String urlPath) {
        this.className = className;
        this.urlPath = urlPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
