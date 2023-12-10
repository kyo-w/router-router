package java.kyodream.handler.memory;


import java.kyodream.RecordManager;
import java.kyodream.handler.MiddlewareHandler;
import java.kyodream.handler.tomcat.TomcatRegistryHandler;
import java.kyodream.record.Context;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.util.HashMap;
import java.util.Map;

public class TomcatMemory extends MiddlewareHandler {
    public static void analysts(Object[] mapperObjects) {
        for (Object mapperObject : mapperObjects) {
            Map<Object, Object> contextObjectToContextVersionMap = (Map<Object, Object>) Reflection.
                    getFieldObjectInParentByName(mapperObject, "contextObjectToContextVersionMap");
            for (Map.Entry<Object, Object> contexts : contextObjectToContextVersionMap.entrySet()) {
                Context context = new Context();
                context.setFrom("tomcat");
                Object key = contexts.getKey();
                Object value = contexts.getValue();
                getPath(context, key);
                TomcatRegistryHandler.getFilterList(context, key);
                getServletList(context, value);
                VirtualPathUtils.getJspUrl(context);
                RecordManager.recordContext(context);
                dyTransformerServlet(context);
            }
        }
    }

    private static void getServletList(Context context, Object value) {
        Object defaultWrapper = Reflection.getFieldObjectInParentByName(value, "defaultWrapper");
        Object[] exactWrappers = (Object[]) Reflection.getFieldObjectInParentByName(value, "exactWrappers");
        Object[] wildcardWrappers = (Object[]) Reflection.getFieldObjectInParentByName(value, "wildcardWrappers");
        Object[] extensionWrappers = (Object[]) Reflection.getFieldObjectInParentByName(value, "extensionWrappers");
        getDefaultMap(context, defaultWrapper);
        getGeneralMap(context, exactWrappers);
        getGeneralMap(context, wildcardWrappers);
        getGeneralMap(context, extensionWrappers);
    }

    private static void getGeneralMap(Context context, Object[] value) {
        String virtualPath = context.getVirtualPath();
        HashMap<String, String> urlMap = context.getUrlMap();
        if (value != null) {
            for (Object mapping : value) {
                String urlRaw = (String) Reflection.getFieldObjectInParentByName(mapping, "name");
                String className = (String) Reflection.getFieldObjectInParentByNameOneByOne(mapping, "object", "servletClass");
                urlMap.put(VirtualPathUtils.concatPath(virtualPath, urlRaw), className);
            }
        }
    }

    private static void getDefaultMap(Context context, Object value) {
        HashMap<String, String> result = new HashMap<>();
        Object targetObject = Reflection.getFieldObjectInParentByName(value, "object");
        Object instance = Reflection.getFieldObjectInParentByName(targetObject, "instance");
        if(instance == null){
            Reflection.invokeMethod(targetObject, "allocate", null, null);
        }
        String className = (String) Reflection.getFieldObjectInParentByName(targetObject, "servletClass");
        result.put(context.getVirtualPath(), className);
        context.setUrlMap(result);
    }

    private static void getPath(Context context, Object key) {
        String path = (String) Reflection.getFieldObjectInParentByName(key, "path");
        String docBase = (String) Reflection.getFieldObjectInParentByName(key, "docBase");
        if (path.equals("")) {
            path = "/";
        }
        context.setVirtualPath(path);
        context.setRootPath(docBase);
    }
}
