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
import java.util.List;

/**
 * RestApi默认监听所有的方法(<init>方法除外)
 */
public class RestApiTransformer extends DyTransformer {

    public RestApiTransformer() {
        //        blackList.add("org.springframework.web.servlet.resource.ResourceHttpRequestHandler");
        blackList.add("org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController");

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
                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                for (InterceptorProcessor interceptor : parse) {
                    interceptor.process(methodProcessor);
                }
            }
            byte[] bytes = AsmUtils.toBytes(classNode, loader, classReader);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addRestApi(String className) {
        return addDyClass(className);
    }

}
