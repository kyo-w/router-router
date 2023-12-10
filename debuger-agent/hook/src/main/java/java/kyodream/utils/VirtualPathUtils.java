package java.kyodream.utils;


import java.io.File;
import java.kyodream.record.Context;
import java.util.HashMap;

public class VirtualPathUtils {

    /**
     * 去除泛路由的*
     * 转换'\'为'/'
     *
     * @param rawPath
     * @return
     */
    public static String formatNativePath(String rawPath) {
        String path = rawPath.replace("\\", "/");
        if (path.endsWith("*")) {
            path = rawPath.substring(0, rawPath.length() - 1);
        }
        return path;
    }

    public static String concatPath(String... paths) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String path : paths) {
            if (path != null) {
                stringBuffer.append("/" + path);
            }
        }
        String path = stringBuffer.toString().replace("\\", "/");
        while (path.contains("//")) {
            path = path.replace("//", "/");
        }
        return path;
    }

    public static void getJspUrl(Context context) {
        String virtualPath = context.getVirtualPath();
        String rootPath = context.getRootPath();
        File fileBase = new File(rootPath);
        File[] files = fileBase.listFiles();
        if (files != null) {
            HashMap<String, String> result = new HashMap<>();
            for (File file : files) {
                if (file.getAbsolutePath().endsWith("jsp") || file.getAbsolutePath().endsWith("jspx")) {
                    result.put(VirtualPathUtils.concatPath(virtualPath, file.getAbsolutePath().substring(rootPath.length())), file.getAbsolutePath());
                }
            }
            context.setJspMap(result);
        }
    }

    public static String fillPath(String virtualPath, String path) {
        if (virtualPath.contains("*")) {
            return virtualPath.replace("*", path).replace("\\", "/").replace("//", "/");
        } else {
            return concatPath(virtualPath, path);
        }
    }
}
