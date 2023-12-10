package kyodream.transformer;


import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import kyodream.annotation.ClassName;
import kyodream.annotation.Handler;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;

import java.kyodream.utils.MethodStruts;
import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;
import java.util.List;

public class BaseTransformer extends CacheTransformer {
    protected String className;
    protected String handlerClassName = null;

    protected Class interceptorClass;

    protected MethodStruts[] methodInfo;

    protected boolean passInitConstructor = false;

    private static ClassLoader routerClassLoader;

    public BaseTransformer() {
        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof ClassName) {
                this.className = ((ClassName) annotation).value();
            }
            if (annotation instanceof TargetInterceptor) {
                this.interceptorClass = ((TargetInterceptor) annotation).value();
            }
            if (annotation instanceof TargetMethod) {
                String[] methodRaw = ((TargetMethod) annotation).value();
                getMethodInfo(methodRaw);
            }
            if (annotation instanceof Handler) {
                this.handlerClassName = ((Handler) annotation).value();
            }
        }
    }


    public BaseTransformer(String className, String handlerClassName, String[] methodName, Class interceptorClass) {
        this.className = className;
        this.handlerClassName = handlerClassName;
        getMethodInfo(methodName);
        this.interceptorClass = interceptorClass;
    }

    public static void setRouterClassLoader(ClassLoader contextClassLoader) {
        routerClassLoader = contextClassLoader;
    }

    public String getClassName() {
        return this.className;
    }

    private void getMethodInfo(String[] value) {
        if (value != null) {
            this.methodInfo = new MethodStruts[value.length];
            for (int i = 0; i < value.length; i++) {
                String[] split = value[i].split(";;");
                this.methodInfo[i] = new MethodStruts();
                this.methodInfo[i].setMethodName(split[0]);
                if (split.length == 1) {
                    this.methodInfo[i].setMethodDesc(null);
                } else {
                    this.methodInfo[i].setMethodDesc(split[1]);
                }
            }
        }
    }

    protected boolean match(String className) {
        return this.className.replace('.', '/').equals(className) ? true : false;
    }

    protected boolean existHandler(ClassLoader loader) {
        if (handlerClassName == null) {
            return true;
        }
        try {
            Class aClass = loader.loadClass(handlerClassName);
            if (aClass != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (loader == routerClassLoader) {
//            不对core.jar的字节码进行增强
            return null;
        }
        if (!match(className)) {
            return null;
        }
        if (!existHandler(loader)) {
            return null;
        }
        beforeCreate(loader);
        byte[] newClassByteCode = createNewClassByteCode(loader, classfileBuffer, this.interceptorClass);
        afterCreate(loader, newClassByteCode);
        return newClassByteCode;
    }

    public void afterCreate(ClassLoader loader, byte[] newClassByteCode) {
    }

    public void beforeCreate(ClassLoader loader) {

    }

    protected byte[] createNewClassByteCode(ClassLoader loader, byte[] oldBytecode, Class interceptorClass) {
        try {
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            ClassReader classReader = AsmUtils.toClassNode(oldBytecode, classNode);
            classNode = AsmUtils.removeJSRInstructions(classNode);
            DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();
            List<InterceptorProcessor> parse = defaultInterceptorClassParser.parse(interceptorClass);
            for (MethodNode methodNode : classNode.methods) {
                // 不提供methodName, 默认增强全部方法
                if (this.methodInfo == null) {
                    // passInitConstructor默认不增强构造函数
                    if (passInitConstructor) {
                        if (methodNode.name.equals("<init>")) {
                            continue;
                        }
                    }
                    MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                    for (InterceptorProcessor interceptor : parse) {
                        interceptor.process(methodProcessor);
                    }
                } else {
                    MethodStruts methodStruts = getMethodStruts(methodNode.name);
                    if (methodStruts != null) {
                        if (methodStruts.getMethodDesc() != null && !methodNode.desc.equals(methodStruts.getMethodDesc())) {
                            continue;
                        }
                        MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                        for (InterceptorProcessor interceptor : parse) {
                            interceptor.process(methodProcessor);
                        }
                    }
                }
            }
            byte[] bytes = AsmUtils.toBytes(classNode, loader, classReader);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private MethodStruts getMethodStruts(String methodName) {
        for (MethodStruts methodStruts : this.methodInfo) {
            if (methodStruts.getMethodName().equals(methodName)) {
                return methodStruts;
            }
        }
        return null;
    }
}
