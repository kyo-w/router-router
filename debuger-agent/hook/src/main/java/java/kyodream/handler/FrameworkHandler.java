package java.kyodream.handler;


import java.kyodream.AgentManager;
import java.kyodream.record.Framework;
import java.kyodream.utils.Reflection;

public class FrameworkHandler extends BaseHandler {
    public static void dyTransformerRestApi(Framework framework) {
        if (framework == null || framework.getHandlerChain() == null) {
            return;
        }
        for (Object handlerChain : framework.getHandlerChain()) {
            String handlerClass = (String) Reflection.getFieldObjectInParentByName(handlerChain, "HandlerClass");
            AgentManager.registryRestApi(handlerClass);
        }
    }
}
