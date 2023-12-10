package java.kyodream;


public class AgentManager {

    private static AgentHook agentHook = null;

    public static void registryHook(AgentHook hook) {
        if (agentHook == null) {
            System.out.println("AgentHook成功");
            agentHook = hook;
        }
    }

    public static void registryServlet(String className) {
        agentHook.registryServlet(className);
    }

    public static void registryRestApi(String handlerClass) {
        agentHook.registryRestApi(handlerClass);
    }

    public abstract static class AgentHook {

        public abstract void registryServlet(String className);

        public abstract void registryRestApi(String handlerClass);
    }
}