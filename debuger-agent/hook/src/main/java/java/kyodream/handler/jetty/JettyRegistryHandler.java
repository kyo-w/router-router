package java.kyodream.handler.jetty;


import java.io.File;
import java.kyodream.RecordManager;
import java.kyodream.handler.MiddlewareHandler;
import java.kyodream.record.Context;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class JettyRegistryHandler extends MiddlewareHandler {
    public static String version;

    public static boolean isJetty8() {
        return JettyRegistryHandler.version.startsWith("8") ? true : false;
    }

    public static void atExit(Object thisObject) {
        analystsContext(thisObject);
    }

    public static void analystsContext(Object thisObject) {
        version = getVersion(thisObject);
        Context context = new Context();
        context.setFrom("jetty");
        getPathInfo(context, thisObject);
        Object servletHandler = Reflection.getFieldObjectInParentByName(thisObject, "_servletHandler");

        getFilters(context, servletHandler);
        getServlet(context, servletHandler);
        VirtualPathUtils.getJspUrl(context);
        RecordManager.recordContext(context);
        dyTransformerServlet(context);
    }

    public static void getServlet(Context context, Object servletHandler) {
        String virtualPath = context.getVirtualPath();
        HashMap<String, String> result = new HashMap<>();
        Object[] servletMappings = (Object[]) Reflection.getFieldObjectInParentByName(servletHandler, "_servletMappings");
        HashMap<String, String[]> servletAlias = getServletAlias(servletMappings);
        Object[] servlets = (Object[]) Reflection.getFieldObjectInParentByName(servletHandler, "_servlets");
        for (Object servlet : servlets) {
            Object servletInstanceObject = Reflection.getFieldObjectInParentByName(servlet, "_servlet");
            if (servletInstanceObject == null) {
                Object config = Reflection.getFieldObjectInParentByName(servlet, "_config");
                initServlet(servlet, config);
            }
            Object name = Reflection.getFieldObjectInParentByName(servlet, "_name");
            String className = (String) Reflection.getFieldObjectInParentByName(servlet, "_className");
            String[] paths = servletAlias.get(name);

            for (String path : paths) {
                String url = VirtualPathUtils.concatPath(virtualPath, path);
                result.put(url, className);
            }
        }
        context.setUrlMap(result);
    }

    private static void initServlet(Object servlet, Object config) {
        try {
            Class<?> aClass = Class.forName("javax.servlet.ServletConfig");
            Reflection.invokeMethod(servlet, "init", new Class[]{aClass}, new Object[]{config});
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<String, String[]> getServletAlias(Object[] servletMappings) {
        HashMap<String, String[]> result = new HashMap<>();
        for (Object servletMap : servletMappings) {
            String[] pathSpecs = (String[]) Reflection.getFieldObjectInParentByName(servletMap, "_pathSpecs");
            String servletName = (String) Reflection.getFieldObjectInParentByName(servletMap, "_servletName");
            result.put(servletName, pathSpecs);
        }
        return result;
    }

    public static void getFilters(Context context, Object servletHandler) {
        Object[] filters = (Object[]) Reflection.getFieldObjectInParentByName(servletHandler, "_filters");
        HashMap<String, String> filterAlias = getFilterAlias(filters);
        Object[] filterMappings = (Object[]) Reflection.getFieldObjectInParentByName(servletHandler, "_filterMappings");
        ArrayList<Object> filterMap = new ArrayList<>();
        if (filterMappings != null) {
            for (Object filter : filterMappings) {
                String[] pathSpecs = (String[]) Reflection.getFieldObjectInParentByName(filter, "_pathSpecs");
                Object filterName = Reflection.getFieldObjectInParentByName(filter, "_filterName");
                for (String path : pathSpecs) {
                    System.out.println(path);
                    System.out.println(filterAlias.get(filterName));
                    filterMap.add(new Context.FilterMap(path, filterAlias.get(filterName)));
                }
            }
        }
        context.setFilterMap(filterMap.toArray(new Context.FilterMap[0]));
    }

    private static HashMap<String, String> getFilterAlias(Object[] filters) {
        HashMap<String, String> result = new HashMap<>();
        for (Object filter : filters) {
            String name = (String) Reflection.getFieldObjectInParentByName(filter, "_name");
            String className = (String) Reflection.getFieldObjectInParentByName(filter, "_className");
            result.put(name, className);
        }
        return result;
    }

    public static void getPathInfo(Context context, Object servletHandler) {
        Object contextPath = Reflection.getFieldObjectInParentByName(servletHandler, "_contextPath");
        Object baseResource = Reflection.getFieldObjectInParentByName(servletHandler, "_baseResource");
        try {
            Path baseDir = (Path) Reflection.getFieldObjectInParentByName(baseResource, "path");
            String absolutePath = baseDir.toFile().getAbsolutePath();
            context.setRootPath(absolutePath);
            context.setVirtualPath((String) contextPath);
        } catch (Exception e) {
            File file = (File) Reflection.getFieldObjectInParentByName(baseResource, "_file");
            String absolutePath = file.getAbsolutePath();
            context.setRootPath(absolutePath);
            context.setVirtualPath((String) contextPath);
        }
    }
}
