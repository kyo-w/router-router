package java.kyodream.handler.struts;


import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.util.LinkedHashMap;

public class Struts2_1RegistryHandler extends BaseStrutsConfigHandler {
    public static void AtExit(Object thisObject) {
        Framework framework = new Framework();
        framework.setFrom("struts");
        Object contextObject = Reflection.getFieldObjectInParentByNameOneByOne(thisObject, "filterConfig", "context");
        getFilterContextPath(contextObject, framework, thisObject);
        getHandlerChain(thisObject, framework);
        dyTransformerRestApi(framework);
        RecordManager.recordFramework(framework);
    }

    private static void getHandlerChain(Object thisObject, Framework framework) {
        LinkedHashMap<String, Object> namespaceObjects =
                (LinkedHashMap<String, Object>) Reflection.getFieldObjectInParentByNameOneByOne(thisObject,
                        "dispatcher", "configurationManager", "configuration", "packageContexts");
        analystModule2Config(namespaceObjects, framework);
    }
}
