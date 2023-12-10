package java.kyodream.handler.memory;


import java.kyodream.handler.jetty.JettyRegistryHandler;

public class JettyMemory {
    public static void analysts(Object[] webAppContextObjects) {
        for (Object webAppContextObject : webAppContextObjects) {
            JettyRegistryHandler.analystsContext(webAppContextObject);
        }
    }
}
