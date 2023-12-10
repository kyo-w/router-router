package java.kyodream.handler.spring;


import java.kyodream.RecordManager;
import java.kyodream.handler.FrameworkHandler;
import java.kyodream.record.Framework;
import java.kyodream.record.SpringHandlerChain;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.lang.reflect.Method;
import java.util.*;

public class SpringRegistryHandler extends FrameworkHandler {

    public static void AtExit(Object thisObject) {
        Framework framework = analystsFramework(thisObject);
        if (framework != null) {
            RecordManager.recordFramework(framework);
            dyTransformerRestApi(framework);
        }
    }

    public static Framework analystsFramework(Object thisObject) {
        Framework framework = new Framework();
        framework.setFrom("spring");
        boolean hasInit = getVirtualPath(framework, thisObject);
        if (hasInit) {
            boolean exist = getRestApi(framework, thisObject);
            if (!exist) {
                System.out.println("未初始化");
                return null;
            }
            return framework;
        }
        return null;
    }

    private static boolean getRestApi(Framework framework, Object thisObject) {
        ArrayList<Object> handlerMappings = (ArrayList<Object>) Reflection.getFieldObjectInParentByName(thisObject, "handlerMappings");
        if (handlerMappings != null) {
            for (Object element : handlerMappings) {
                String name = element.getClass().getName();
                if (name.contains("RequestMappingHandlerMapping")) {
                    handlerRequestMappingHandlerMapping(element, framework);
                }
                if (name.contains("BeanNameUrlHandlerMapping") ||
                        name.contains("SimpleUrlHandlerMapping") ||
                        name.contains("ControllerClassNameHandlerMapping") ||
                        name.contains("ControllerBeanNameHandlerMapping")) {
                    handlerBeanNameUrlHandlerMapping(element, framework);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @param element
     * @param framework
     * @Bean类型注册
     */
    private static void handlerBeanNameUrlHandlerMapping(Object element, Framework framework) {
        String virtualPath = framework.getVirtualPath();
        List<Object> springHandlerChains = new ArrayList<>();
        LinkedHashMap<Object, Object> handlerMap = (LinkedHashMap<Object, Object>) Reflection.getFieldObjectInParentByName(element, "handlerMap");
        for (Map.Entry<Object, Object> map : handlerMap.entrySet()) {
            String rawUrl = (String) map.getKey();
            String className = map.getValue().getClass().getName();
            SpringHandlerChain springHandlerChain = new SpringHandlerChain();
            springHandlerChain.setUrl(VirtualPathUtils.fillPath(virtualPath, rawUrl));
            springHandlerChain.setHandlerClass(className);
            springHandlerChains.add(springHandlerChain);
        }
        if (framework.getHandlerChain() == null) {
            framework.setHandlerChain(springHandlerChains.toArray());
        } else {
            springHandlerChains.addAll(Arrays.asList(framework.getHandlerChain()));
            framework.setHandlerChain(springHandlerChains.toArray());
        }
    }

    /**
     * 注解注册
     *
     * @param element
     * @param framework
     */
    private static void handlerRequestMappingHandlerMapping(Object element, Framework framework) {
        String virtualPath = framework.getVirtualPath();
        List<Object> springHandlerChains = new ArrayList<>();
        Object mappingRegistry = Reflection.getFieldObjectInParentByName(element, "mappingRegistry");
        HashMap<Object, Object> registry = (HashMap<Object, Object>) Reflection.getFieldObjectInParentByName(mappingRegistry, "registry");
        for (Map.Entry<Object, Object> map : registry.entrySet()) {
            String url = getUrlByVersion(map.getKey());
            Object handlerMethod = Reflection.getFieldObjectInParentByName(map.getValue(), "handlerMethod");
            Method method = (Method) Reflection.getFieldObjectInParentByName(handlerMethod, "method");
            String className = method.getDeclaringClass().getName();
            SpringHandlerChain springHandlerChain = new SpringHandlerChain();
            springHandlerChain.setUrl(VirtualPathUtils.fillPath(virtualPath, url));
            springHandlerChain.setHandlerClass(className);
            springHandlerChains.add(springHandlerChain);
        }
        if (framework.getHandlerChain() == null) {
            framework.setHandlerChain(springHandlerChains.toArray());
        } else {
            springHandlerChains.addAll(Arrays.asList(framework.getHandlerChain()));
            framework.setHandlerChain(springHandlerChains.toArray());
        }
    }

    public static String getUrlByVersion(Object testObject) {
        String url = null;
        Object patternsCondition = Reflection.getFieldObjectInParentByName(testObject, "patternsCondition");
        if (patternsCondition != null) {
            LinkedHashSet<Object> patterns = (LinkedHashSet<Object>) Reflection.getFieldObjectInParentByName(patternsCondition, "patterns");
            url = (String) patterns.toArray()[0];
        } else {
            TreeSet<Object> patternsObject = (TreeSet<Object>) Reflection.getFieldObjectInParentByNameOneByOne(testObject, "pathPatternsCondition", "patterns");
            url = (String) Reflection.getFieldObjectInParentByName(patternsObject.toArray()[0], "patternString");
        }
        return url;
    }
}
