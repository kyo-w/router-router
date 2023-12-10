package kyodream.transformer;


import kyodream.AgentManagerImpl;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * 所有ClassLoader加载器HOOK
 */
public class ClassLoaderTransformer implements ClassFileTransformer {
    private ClassLoader classLoader;

    public ClassLoaderTransformer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (loader != null && loader != classLoader) {
            AgentManagerImpl.registryClassLoad(loader);
        }
        return null;
    }
}
