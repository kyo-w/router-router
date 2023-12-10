package java.kyodream.handler.jersey;

import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.util.HashMap;

public class JerseyRegistryHandler extends BaseJerseyHandler {

    public static void AtExit(Object thisObject) {
        Framework framework = new Framework();
        framework.setFrom("jersey");
        boolean hasInit = getVirtualPath(framework, thisObject);
        if (hasInit) {
            getApiUrl(framework, thisObject);
            dyTransformerRestApi(framework);
            RecordManager.recordFramework(framework);
        }
    }

    private static void getApiUrl(Framework framework, Object frameworkObject) {
        String virtualPath = framework.getVirtualPath();
        HashMap<Object, Object> abstractResourceMap = (HashMap<Object, Object>) Reflection.
                getFieldObjectInParentByNameOneByOne(frameworkObject, "webComponent", "application", "abstractResourceMap");
        Object[] result = analystsClass(virtualPath, abstractResourceMap.keySet());
        framework.setHandlerChain(result);
    }
}
