package java.kyodream.handler.struts;


import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.util.LinkedHashMap;

/**
 * 2.3 < Struts2  < 2.5
 */
public class Struts2RegistryHandler extends BaseStrutsConfigHandler {
    public static void AtExit(Object thisObject, boolean is25) {
        Framework framework = new Framework();
        framework.setFrom("struts");
        Object contextObject = Reflection.getFieldObjectInParentByNameOneByOne(thisObject, "execute",
                "dispatcher", "servletContext", "context", "context");
        if (is25) {
            getFilterContextPath(contextObject, framework, thisObject);
        } else {
            getFilterContextPath(contextObject, framework, thisObject);
        }
        getHandlerChain(thisObject, framework);
        dyTransformerRestApi(framework);
        RecordManager.recordFramework(framework);

    }

    private static void getHandlerChain(Object thisObject, Framework framework) {
        LinkedHashMap<String, Object> namespaceObjects = (LinkedHashMap<String, Object>) Reflection.getFieldObjectInParentByNameOneByOne(thisObject,
                "execute", "dispatcher", "configurationManager", "configuration", "packageContexts");
        analystModule2Config(namespaceObjects, framework);
    }
}
