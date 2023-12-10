package kyodream.transformer;


import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.Decompiler;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import kyodream.interceptor.TraceInterceptor;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServletTransformer extends DyTransformer {
    private static Set<String> methodNames = new HashSet<String>();

    static {
        methodNames.add("doGet");
        methodNames.add("doHead");
        methodNames.add("doPost");
        methodNames.add("doPut");
        methodNames.add("doDelete");
        methodNames.add("doOptions");
        methodNames.add("doTrace");
    }

    public ServletTransformer() {
//        jersey2 分析对象本身不HOOK
        blackList.add("org.glassfish.jersey.servlet.ServletContainer");
//        jersey 分析对象本身不HOOK
        blackList.add("com.sun.jersey.spi.container.servlet.ServletContainer");
//        tomcat默认处理器不HOOk
        blackList.add("org.apache.catalina.servlets.DefaultServlet");
//        jersey wadl默认不HOOK
        blackList.add("com.sun.jersey.server.impl.wadl.WadlResource");
//        NoJspServlet默认不HOOK
        blackList.add("org.eclipse.jetty.servlet.NoJspServlet");
//        jetty默认处理类不HOOK
        blackList.add("org.eclipse.jetty.servlet.DefaultServlet");
//        spring 分析对象本身不HOOK
        blackList.add("org.springframework.web.servlet.DispatcherServlet");
        blackList.add("org.apache.struts.action.ActionServlet");
        blackList.add("org.apache.jasper.servlet.JspServlet");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (loader == routerClassLoader) {
            return null;
        }
        if (!match(className)) {
            return null;
        }

        System.out.println("动态注册: " + className + ", classLoader: " + loader);
        try {
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
            classNode = AsmUtils.removeJSRInstructions(classNode);
            DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();
            List<InterceptorProcessor> parse = defaultInterceptorClassParser.parse(TraceInterceptor.class);
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals("<init>")) {
                    continue;
                }
                if (!methodNames.contains(methodNode.name)) {
                    continue;
                }
                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                for (InterceptorProcessor interceptor : parse) {
                    interceptor.process(methodProcessor);
                }
            }
            byte[] bytes = AsmUtils.toBytes(classNode, loader, classReader);
            return bytes;
        } catch (Exception | Error e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addServlet(String className) {
        return addDyClass(className);
    }
}
