package java.kyodream.handler.struts;


import java.kyodream.RecordManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;

public class StrutsRegistryHandler extends BaseStrutsConfigHandler {
    public static void AtExit(Object thisObject) {
        Framework framework = new Framework();
        framework.setFrom("struts");
        String contextPath = servletGetContextPath(thisObject);
        String servletMapping = Reflection.getFieldObjectInParentByName(thisObject, "servletMapping").toString();
        framework.setVirtualPath(VirtualPathUtils.concatPath(contextPath, servletMapping));
        getHandlerChain(servletGetContextAttribute(thisObject, "org.apache.struts.action.MODULE"), framework);
        dyTransformerRestApi(framework);
        RecordManager.recordFramework(framework);

    }

    private static void getHandlerChain(Object servletGetContextAttribute, Framework framework) {
        analystModuleConfig(servletGetContextAttribute, framework);
    }
}
