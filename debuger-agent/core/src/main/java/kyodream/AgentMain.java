package kyodream;

import kyodream.controller.LogController;
import kyodream.transformer.ClassLoaderTransformer;
import kyodream.transformer.ServletRegistryTransformer;
import kyodream.transformer.jersey.Jersey2RegistryTransformer;
import kyodream.transformer.jersey.JerseyRegistryTransformer;
import kyodream.transformer.jetty.JettyFilterStreamTransformer;
import kyodream.transformer.jetty.JettyHttpStreamTransformer;
import kyodream.transformer.jetty.JettyRegistryTransformer;
import kyodream.transformer.spring.SpringMvcRegistry;
import kyodream.transformer.struts.Struts2RegistryTransformer;
import kyodream.transformer.struts.Struts2_1RegistryTransformer;
import kyodream.transformer.struts.Struts2_5RegistryTransformer;
import kyodream.transformer.struts.StrutsRegistryTransformer;
import kyodream.transformer.tomcat.TomcatHttpStreamTransformer;
import kyodream.transformer.tomcat.TomcatRegistryTransformer;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.kyodream.AgentManager;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;

/**
 * Tomcat 8/9
 * Spring 4/5
 * Jetty > 8
 * Jersey 1.x
 * Jersey 2.x
 * Struts 2.x
 * Struts 1.x
 */
public class AgentMain {

    public static void main(String agentArgs, Instrumentation inst) throws IOException {
        HashMap<String, String> args = getAgentArgs(agentArgs);
        System.setOut(LogController.printStream);
        System.setErr(LogController.printStream);
        WebServer.initServer(args);
        AgentManagerImpl agentManager = new AgentManagerImpl();
        RecordManagerImpl recordManager = new RecordManagerImpl();
        initHook(agentManager, recordManager);
        AgentManagerImpl.init(inst);

//        加载器记录器
        inst.addTransformer(new ClassLoaderTransformer(Thread.currentThread().getContextClassLoader()));
        middlewareRegistry();

//        SpringMvc记录器
        AgentManagerImpl.registryStaticTransformer(new SpringMvcRegistry());
//        Jersey2记录器
        AgentManagerImpl.registryStaticTransformer(new Jersey2RegistryTransformer());
//        Jersey记录器
        AgentManagerImpl.registryStaticTransformer(new JerseyRegistryTransformer());
//        Struts记录器
        AgentManagerImpl.registryStaticTransformer(new Struts2RegistryTransformer());
        AgentManagerImpl.registryStaticTransformer(new Struts2_1RegistryTransformer());
        AgentManagerImpl.registryStaticTransformer(new Struts2_5RegistryTransformer());
        AgentManagerImpl.registryStaticTransformer(new StrutsRegistryTransformer());
    }

    private static HashMap<String, String> getAgentArgs(String agentArgs) {
        Base64 base64 = new Base64();
        String rawAgentArgs = null;
        rawAgentArgs = new String(base64.decode(agentArgs));
        String[] args = rawAgentArgs.substring(0, rawAgentArgs.length() - 1).substring(1)
                .split("\\|\\|");
        HashMap<String, String> argMap = new HashMap<>();
        for (String arg : args) {
            String[] split = arg.split("@");
            argMap.put(split[0], split[1]);
        }
        return argMap;
    }

    /**
     * 中间件负责Context上下文处理/Http流请求记录/以及上下文中注册的Servlet/RestApi增强
     */
    private static void middlewareRegistry() {
//        Tomcat上下文处理器
        AgentManagerImpl.registryStaticTransformer(new TomcatRegistryTransformer());
//        Tomcat请求流记录器
        AgentManagerImpl.registryStaticTransformer(new TomcatHttpStreamTransformer());
//        Tomcat addServlet方法记录器
        AgentManagerImpl.registryStaticTransformer(new ServletRegistryTransformer());


//        Jetty上下文处理器
        AgentManagerImpl.registryStaticTransformer(new JettyRegistryTransformer());

        // Jetty流记录分为两个Transformer: JettyFilterStreamTransformer/ JettyHttpStreamTransformer
        AgentManagerImpl.registryStaticTransformer(new JettyFilterStreamTransformer());
        AgentManagerImpl.registryStaticTransformer(new JettyHttpStreamTransformer());
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            main(agentArgs, inst);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Memory.loadMemoryObject();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            main(agentArgs, inst);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initHook(AgentManagerImpl agentManager, RecordManagerImpl recordManagerImpl) {
        AgentManager.registryHook(agentManager);
        recordManagerImpl.registryHook();
    }

}
