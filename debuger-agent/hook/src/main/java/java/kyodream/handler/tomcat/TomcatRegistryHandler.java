package java.kyodream.handler.tomcat;

import java.kyodream.RecordManager;
import java.kyodream.handler.MiddlewareHandler;
import java.kyodream.record.Context;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TomcatRegistryHandler extends MiddlewareHandler {

    public static String version;

    public static void atEnter(Object thisObject, Object[] args) {
        version = getVersion(thisObject);
        Context tomcatContext = new Context();
        tomcatContext.setFrom("tomcat");
        getRootPath(tomcatContext, args[6]);
        getVirtualPath(tomcatContext, (String) args[2]);
        VirtualPathUtils.getJspUrl(tomcatContext);
        getFilterList(tomcatContext, args[4]);
        getServletList(tomcatContext, (ArrayList<Object>) args[7]);
        RecordManager.recordContext(tomcatContext);
        dyTransformerServlet(tomcatContext);
    }

    private static void getVirtualPath(Context tomcatContext, String arg) {
        if (arg == null || arg.equals("")) {
            tomcatContext.setVirtualPath("/");
        } else {
            tomcatContext.setVirtualPath(arg);
        }
    }

    private static void getRootPath(Context context, Object webResourceObject) {
        Object main = Reflection.getFieldObjectInParentByName(webResourceObject, "main");
        context.setRootPath((String) Reflection.getFieldObjectInParentByName(main, "base"));
    }

    private static void getServletList(Context context, ArrayList<Object> wrappersObject) {
        String virtualPath = context.getVirtualPath();
        HashMap<String, String> result = new HashMap<>();
        for (Object element : wrappersObject) {
            String urlMap = (String) Reflection.getFieldObjectInParentByName(element, "mapping");
            Object wrapper = Reflection.getFieldObjectInParentByName(element, "wrapper");
            String servletClass = (String) Reflection.getFieldObjectInParentByName(wrapper, "servletClass");
            String urlPath = VirtualPathUtils.concatPath(virtualPath, urlMap);
            result.put(urlPath, servletClass);
        }
        context.setUrlMap(result);
    }

    public static void getFilterList(Context context, Object contextObject) {
        String path = context.getVirtualPath();
        HashMap<String, String> filterAlias = getFilterAlias(contextObject, path);
        context.setFilterMap(internalGetFilterList(contextObject, filterAlias));
    }

    private static Context.FilterMap[] internalGetFilterList(Object contextObject, HashMap<String, String> filterAlias) {
        ArrayList<Context.FilterMap> result = new ArrayList<>();
        HashMap<Object, Object> filterDefs = (HashMap<Object, Object>) Reflection.getFieldObjectInParentByName(contextObject, "filterDefs");
        for (Map.Entry<Object, Object> filter : filterDefs.entrySet()) {
            if (filter == null) {
                continue;
            }
            Object key = filter.getKey();
            Object value = filter.getValue();
            String filterClass = (String) Reflection.getFieldObjectInParentByName(value, "filterClass");
            String url = filterAlias.get(key);
            System.out.println(url);
            System.out.println(filterClass);
            result.add(new Context.FilterMap(filterAlias.get(key), filterClass));
        }
        return result.toArray(new Context.FilterMap[0]);
    }


    private static HashMap<String, String> getFilterAlias(Object contextObject, String path) {
        HashMap<String, String> result = new HashMap<>();
        Object filterMaps = Reflection.getFieldObjectInParentByName(contextObject, "filterMaps");
        Object[] array = (Object[]) Reflection.getFieldObjectInParentByName(filterMaps, "array");
        for (Object filterMap : array) {
            String filterName = (String) Reflection.getFieldObjectInParentByName(filterMap, "filterName");
            Object[] urlPatterns = (Object[]) Reflection.getFieldObjectInParentByName(filterMap, "urlPatterns");
            if (urlPatterns.length > 0) {
                result.put(filterName, VirtualPathUtils.concatPath(path, (String) urlPatterns[0]));
            } else {
                result.put(filterName, VirtualPathUtils.concatPath(path, "EMPTY"));
            }
        }
        return result;
    }
}