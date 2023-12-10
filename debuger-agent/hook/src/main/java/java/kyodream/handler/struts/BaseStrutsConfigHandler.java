package java.kyodream.handler.struts;


import java.kyodream.handler.FrameworkHandler;
import java.kyodream.record.Framework;
import java.kyodream.record.StrutsHandlerChain;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.util.*;

public class BaseStrutsConfigHandler extends FrameworkHandler {

    /**
     * Struts1
     *
     * @param moduleConfig
     * @param framework
     */
    protected static void analystModuleConfig(Object moduleConfig, Framework framework) {
        String virtualPath = framework.getVirtualPath();
        String prefix = Reflection.getFieldObjectInParentByName(moduleConfig, "prefix").toString();
        HashMap<String, Object> actionConfigs = (HashMap<String, Object>) Reflection.getFieldObjectInParentByName(moduleConfig, "actionConfigs");
        ArrayList<Object> map = new ArrayList<>();
        for (Map.Entry<String, Object> actionConfig : actionConfigs.entrySet()) {
            Object actionMapping = actionConfig.getValue();
            String path = Reflection.getFieldObjectInParentByName(actionMapping, "path").toString();
            String type = Reflection.getFieldObjectInParentByName(actionMapping, "type").toString();
            StrutsHandlerChain strutsHandlerChain = new StrutsHandlerChain();
            // context + prefix + mapPath
            strutsHandlerChain.setUrl(VirtualPathUtils.fillPath(virtualPath, VirtualPathUtils.concatPath(prefix, path)));
            strutsHandlerChain.setHandlerClass(type);
            map.add(strutsHandlerChain);
        }
        framework.setHandlerChain(map.toArray());
    }

    /**
     * Struts2
     */
    protected static void analystModule2Config(LinkedHashMap<String, Object> namespaceObjects, Framework framework) {
        ArrayList<Object> result = new ArrayList<>();
        for (Object namespaceObject : namespaceObjects.values()) {
            ArrayList<Object> listMap = analystNamespaceObject(namespaceObject, framework);
            if (listMap != null) {
                result.addAll(listMap);
            }
        }
        framework.setHandlerChain(result.toArray());
    }

    protected static ArrayList<Object> analystNamespaceObject(Object namespaceObject, Framework framework) {
        String virtualPath = framework.getVirtualPath();
        Map<String, Object> actionConfigs = (Map<String, Object>) Reflection.getFieldObjectInParentByName(namespaceObject, "actionConfigs");
        if (actionConfigs.size() == 0) {
            return null;
        }
        ArrayList<Object> result = new ArrayList<>();
        for (Map.Entry<String, Object> actionConfig : actionConfigs.entrySet()) {
            Object value = actionConfig.getValue();
            String className = (String) Reflection.getFieldObjectInParentByName(value, "className");
            List<Object> interceptors = (List<Object>) Reflection.getFieldObjectInParentByName(value, "interceptors");

            StrutsHandlerChain strutsHandlerChains = new StrutsHandlerChain();
            strutsHandlerChains.setHandlerClass(className);
            strutsHandlerChains.setUrl(VirtualPathUtils.fillPath(virtualPath, actionConfig.getKey()));
            if (interceptors != null && interceptors.size() > 0) {
                String[] interceptorRecord = new String[interceptors.size()];
                int interceptorPoint = 0;
                for (Object interceptor : interceptors) {
                    interceptorRecord[interceptorPoint] = Reflection.getFieldObjectInParentByName(interceptor, "interceptor").getClass().getName();
                }
                strutsHandlerChains.setInterceptor(interceptorRecord);
            }
            result.add(strutsHandlerChains);
        }
        return result;
    }

    protected static void getFilterContextPath(Object contextObject, Framework framework, Object thisObject) {
        String contextPath = Reflection.getFieldObjectInParentByName(contextObject, "path").toString();
        String mapUrl = null;
        HashMap<String, Object> filterDefs = (HashMap<String, Object>) Reflection.getFieldObjectInParentByName(contextObject, "filterDefs");
        String aliasName = null;
        for (Map.Entry<String, Object> filterDef : filterDefs.entrySet()) {
            if (Reflection.getFieldObjectInParentByName(filterDef.getValue(), "filterClass").toString()
                    .equals(thisObject.getClass().getName())) {
                aliasName = filterDef.getKey();
            }
        }
        Object[] filterObjects = (Object[]) Reflection.getFieldObjectInParentByNameOneByOne(contextObject, "filterMaps", "array");
        for (Object filterObject : filterObjects) {
            if (Reflection.getFieldObjectInParentByName(filterObject, "filterName").equals(aliasName)) {
                mapUrl = ((Object[]) Reflection.getFieldObjectInParentByName(filterObject, "urlPatterns"))[0].toString();
            }
        }
        framework.setVirtualPath(VirtualPathUtils.concatPath(contextPath, mapUrl));
    }

}
