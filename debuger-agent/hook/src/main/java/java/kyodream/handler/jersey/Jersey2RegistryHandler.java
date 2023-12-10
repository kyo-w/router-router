package java.kyodream.handler.jersey;


import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.util.Collection;

public class Jersey2RegistryHandler extends BaseJerseyHandler {
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
        Object configuration = Reflection.invokeMethod(frameworkObject, "getConfiguration", null, null);
        Collection<Object> cachedClasses = (Collection<Object>) Reflection.invokeMethod(configuration, "getClasses", null, null);
        Object[] result = analystsClass(virtualPath, cachedClasses);
        framework.setHandlerChain(result);
    }

}
