package kyodream.router;


import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.jar.JarFile;

public class Main {
    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        main(agentArgs, inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        main(agentArgs, inst);
    }

    private static void main(String agentArgs, Instrumentation inst) throws Exception {
        HashMap<String, String> args = getAgentArgs(agentArgs);
        String pwd = args.get("--work-path");
        System.out.println(pwd);
        boolean alreadyInit = init(pwd, inst);
        if (alreadyInit) {
            return;
        }
        File file = new File(Paths.get(pwd, "core.jar").toString());
        URL url = file.toURI().toURL();
        RouterRouterClassLoader routerRouterClassLoader = new RouterRouterClassLoader(new URL[]{url});
        Thread.currentThread().setContextClassLoader(routerRouterClassLoader);
        Class agentMainClass = Class.forName("kyodream.AgentMain", true, routerRouterClassLoader);
        Method agentmain = agentMainClass.getDeclaredMethod("agentmain", String.class, Instrumentation.class);
        agentmain.invoke(null, agentArgs, inst);
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

    private static boolean init(String pwd, Instrumentation inst) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName("java.kyodream.AgentManager");
        } catch (ClassNotFoundException e) {
        }
        if (aClass != null) {
            return true;
        }
        try {
            JarFile jarFile = new JarFile(Paths.get(pwd, "hook.jar").toString());
            inst.appendToBootstrapClassLoaderSearch(jarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
