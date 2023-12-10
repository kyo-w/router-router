package java.kyodream.handler.common;


import java.kyodream.AgentManager;

public class ServletRegistryHandler {
    public static void atEnter(Object[] object) {
        AgentManager.registryServlet(object[2].getClass().getName());
    }
}
