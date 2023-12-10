package java.kyodream.handler;

import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.util.HashMap;
import java.util.Map;

//所有中间件 /框架的最终父类
public class BaseHandler {
    public static String getVersion(Object thisObject) {
        return thisObject.getClass().getPackage().getImplementationVersion();
    }


    /**
     * 中间件无关操作
     *
     * @param servlet
     * @return
     */
    public static Object servletGetContextObject(Object servlet) {
        try {
            return Reflection.invokeMethod(servlet, "getServletContext", null, null);
        } catch (Exception e) {
            System.out.println("servlet还未初始化");
        }
        return null;
    }

    /**
     * 中间件无关操作
     *
     * @param servlet
     * @return
     */
    public static String servletGetContextPath(Object servlet) {
        Object context = servletGetContextObject(servlet);
        return Reflection.invokeMethod(context, "getContextPath", null, null).toString();
    }

    /**
     * 中间件无关操作
     *
     * @param servlet
     * @return
     */
    public static Object servletGetContextAttribute(Object servlet, String attr) {
        Object context = servletGetContextObject(servlet);
        return Reflection.invokeMethod(context, "getAttribute", new Class[]{String.class}, new Object[]{attr});
    }

    protected static boolean getVirtualPath(Framework framework, Object thisObject) {
        Object contextObject = servletGetContextObject(thisObject);
        if (contextObject == null) {
            return false;
        }
        String contextPath = servletGetContextPath(thisObject);

        System.out.println(contextObject.getClass().getName());
        if (contextObject.getClass().getName().contains("jetty")) {
            getVirtualPathInJetty(framework, contextPath, contextObject, thisObject);
        } else {
            getVirtualPathInTomcat(framework, contextPath, contextObject, thisObject);
        }
        return true;
    }

    private static void getVirtualPathInTomcat(Framework framework, String contextPath, Object contextObject, Object thisObject) {
        HashMap<String, String> servletMappings = (HashMap<String, String>) Reflection.getFieldObjectInParentByNameOneByOne(contextObject,
                "context", "context", "servletMappings");
        HashMap<String, Object> getServletRegistrations = (HashMap<String, Object>) Reflection.invokeMethod(contextObject,
                "getServletRegistrations", null, null);
        for (Map.Entry<String, Object> map : getServletRegistrations.entrySet()) {
            if (Reflection.getFieldObjectInParentByNameOneByOne(map.getValue(), "wrapper", "servletClass").toString()
                    .equals(thisObject.getClass().getName())) {
                String name = map.getKey();
                for (Map.Entry<String, String> servletMap : servletMappings.entrySet()) {
                    if (name.equals(servletMap.getValue())) {
                        String path = servletMap.getKey();
                        framework.setVirtualPath(VirtualPathUtils.concatPath(contextPath, path));
                    }
                }
            }
        }
    }

    private static void getVirtualPathInJetty(Framework framework, String contextPath, Object contextObject, Object thisObject) {
        String name = thisObject.getClass().getName();
        Object servletHolderObject = Reflection.getFieldObjectInParentByName(contextObject, "this$0");
        Object servletHandler = Reflection.getFieldObjectInParentByName(servletHolderObject, "_servletHandler");
        Object[] servletMapping = (Object[]) Reflection.getFieldObjectInParentByName(servletHandler, "_servletMappings");
        HashMap<String, Object> servletNameMap = (HashMap<String, Object>) Reflection.getFieldObjectInParentByName(servletHandler, "_servletNameMap");
        String aliasName = null;
        for (Map.Entry<String, Object> servletNameObject : servletNameMap.entrySet()) {
            String className = Reflection.getFieldObjectInParentByName(servletNameObject.getValue(), "_className").toString();
            if (className.equals(name)) {
                aliasName = servletNameObject.getKey();
            }
        }
        for (Object servlet : servletMapping) {
            if (Reflection.getFieldObjectInParentByName(servlet, "_servletName").equals(aliasName)) {
                String path = ((String[]) Reflection.getFieldObjectInParentByName(servlet, "_pathSpecs"))[0];
                framework.setVirtualPath(VirtualPathUtils.concatPath(contextPath, path));
            }
        }
    }
}