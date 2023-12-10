package kyodream.record;

public class FilterRecord {
    private String className;
    private String urlPath;

    public FilterRecord(String className, String urlPath) {
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
}
