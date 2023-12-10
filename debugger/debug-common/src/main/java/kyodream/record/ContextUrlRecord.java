package kyodream.record;

import java.util.Objects;

public class ContextUrlRecord {
    private UrlType urlType;
    private String virtualPath;
    private String urlPath;

    public ContextUrlRecord(UrlType urlType, String virtualPath, String urlPath) {
        this.urlType = urlType;
        this.virtualPath = virtualPath;
        this.urlPath = urlPath;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getPattern() {
        switch (urlType) {
            case EXACT:
            case WILD:
                return concatSubPath(virtualPath, "/", urlPath);
            case EXT:
                return concatSubPath(virtualPath, "/*" + urlPath);
            default:
                return "";
        }
    }

    public String concatContextPathAndSubPath(String subPath) {
        if (virtualPath.equals("")) {
            virtualPath = "/";
        }
        switch (getUrlType()) {
            case EXACT:
                return concatSubPath(getVirtualPath(), getUrlPath());
            case EXT:
                return concatSubPath(getVirtualPath(), subPath + "." + getUrlPath());
            case WILD:
                return concatSubPath(getVirtualPath(), getUrlPath(), subPath);
            default:
                return "";
        }
    }

    public static ContextUrlRecord getContextUrlRecord(String virtualPath, String path) {
        if (path.contains("*")) {
            if (path.startsWith("*.")) {
                return new ContextUrlRecord(ContextUrlRecord.UrlType.EXT, virtualPath, path.substring(2));
            } else {
                return new ContextUrlRecord(ContextUrlRecord.UrlType.WILD, virtualPath, path.substring(0, path.length() - 1));
            }
        } else {
            return new ContextUrlRecord(ContextUrlRecord.UrlType.EXACT, virtualPath, path);
        }
    }

    public static String concatSubPath(String... paths) {
        StringBuffer result = new StringBuffer();
        for (String path : paths) {
            result.append(path);
        }
        String goodPath = result.toString();
        while (goodPath.contains("\\")) {
            goodPath = goodPath.replaceAll("\\\\", "/");
        }
        while (goodPath.contains("//")) {
            goodPath = goodPath.replaceAll("//", "/");
        }
        return goodPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextUrlRecord urlRecord = (ContextUrlRecord) o;
        return Objects.equals(virtualPath, urlRecord.virtualPath) && Objects.equals(urlPath, urlRecord.urlPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(virtualPath, urlPath);
    }

    public enum UrlType {
        DEFAULT,
        EXACT,
        WILD,
        EXT
    }
}
