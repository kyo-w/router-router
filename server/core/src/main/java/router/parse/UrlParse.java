package router.parse;


import java.util.Objects;

public class UrlParse {
    private UrlType urlType;
    private String virtualPath;
    private String basePath;
    public boolean frameworkMode;

    public UrlParse(UrlType urlType, String virtualPath, String basePath) {
        this.urlType = urlType;
        this.virtualPath = virtualPath;
        this.basePath = basePath;
    }

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isFrameworkMode() {
        return frameworkMode;
    }

    public void setFrameworkMode(boolean frameworkMode) {
        this.frameworkMode = frameworkMode;
    }

    public String getPath() {
        switch (urlType) {
            case EXACT:
            case WILD:
                return concatSubPath(virtualPath, "/", basePath );
            case EXT:
                return concatSubPath(virtualPath, "/*" + basePath);
            default:
                return "";
        }
    }

    public String getPathByMiddleware(String path){
        if (virtualPath.equals("")) {
            virtualPath = "/";
        }
        switch (getUrlType()) {
            case EXACT:
                return concatSubPath(getVirtualPath(), getBasePath());
            case EXT:
                return concatSubPath(getVirtualPath(), path + "." + getBasePath());
            case WILD:
                return concatSubPath(getVirtualPath(), getBasePath(), path);
            default:
                if (frameworkMode) {
                    return concatSubPath("", path);
                } else {
                    return "";
                }
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

    public static UrlParse getMiddlewareParse(String virtualPath, String path){
        if (path.contains("*")) {
            if (path.startsWith("*.")) {
                return new UrlParse(UrlType.EXT, virtualPath, path.substring(2));
            } else {
                return new UrlParse(UrlType.WILD, virtualPath, path.substring(0, path.length() - 1));
            }
        } else {
            return new UrlParse(UrlType.EXACT, virtualPath, path);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlParse urlRecord = (UrlParse) o;
        return Objects.equals(virtualPath, urlRecord.virtualPath) && Objects.equals(basePath, urlRecord.basePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(virtualPath, basePath);
    }

}
