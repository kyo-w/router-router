package kyodream;

import kyodream.transformer.BaseTransformer;
import kyodream.transformer.DyTransformer;
import kyodream.transformer.RestApiTransformer;
import kyodream.transformer.ServletTransformer;

import java.kyodream.AgentManager;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AgentManagerImpl extends AgentManager.AgentHook {
    private static Instrumentation instrumentation;

//    JVM中所有的ClassLoader
    private static CopyOnWriteArraySet<ClassLoader> classLoaders = new CopyOnWriteArraySet<>();

//    动态注册Transformer
    private static ServletTransformer servletTransformer;
    private static RestApiTransformer restApiTransformer;

//    所有Class对象缓存
    private static HashMap<String, Set<Class>> classHashMap = new HashMap<>();

//    缓存所有经过增强的Class对象
    private static HashSet<Class> transformerCache = new HashSet<>();

    public static void registryClassLoad(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }


    public static void initClassLoader(Instrumentation inst) {
        instrumentation = inst;
        for (Class classObject : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = classObject.getClassLoader();
            if (classLoader == Thread.currentThread().getContextClassLoader()) {
                continue;
            }
            if (classLoader != null) {
                classLoaders.add(classLoader);
                Set<Class> classes = classHashMap.get(classObject.getName());
                if (classes == null) {
                    classes = new HashSet<>();
                    classHashMap.put(classObject.getName(), classes);
                }
                classes.add(classObject);
            }
        }
    }

    public static void registryStaticTransformer(BaseTransformer baseTransformer) {
        System.out.println("静态Hook成功: " + baseTransformer.getClassName());
        instrumentation.addTransformer(baseTransformer, true);
        transformClassObject(baseTransformer.getClassName());
    }


    @Override
    public void registryServlet(String className) {
        boolean canReTransform = servletTransformer.addServlet(className);
        if (canReTransform) {
            transformClassObject(className);
        }
    }

    @Override
    public void registryRestApi(String handlerClass) {
        boolean canReTransform = restApiTransformer.addRestApi(handlerClass);
        if (canReTransform) {
            transformClassObject(handlerClass);
        }
    }

    private static void transformClassObject(String className) {
        for (ClassLoader classLoader : classLoaders) {
            try {
                Class aClass = classLoader.loadClass(className);
                boolean modifiableClass = instrumentation.isModifiableClass(aClass);
                if (!modifiableClass) {
                    System.out.println(className + "不支持转化");
                    continue;
                }
                if (!transformerCache.contains(aClass)) {
                    transformerCache.add(aClass);
                    instrumentation.retransformClasses(aClass);
                }
            } catch (ClassNotFoundException e) {
            } catch (UnmodifiableClassException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 初始化ClassLoader和Classes缓存
     * 初始化动态增强的Transformer
     *
     * @param inst
     */
    public static void init(Instrumentation inst) {
        initClassLoader(inst);
        servletTransformer = new ServletTransformer();
        restApiTransformer = new RestApiTransformer();
        inst.addTransformer(servletTransformer, true);
        inst.addTransformer(restApiTransformer, true);

        BaseTransformer.setRouterClassLoader(Thread.currentThread().getContextClassLoader());
        DyTransformer.setRouterClassLoader(Thread.currentThread().getContextClassLoader());

    }

    public static Set<Class> getClasses(String classNames) {
        return classHashMap.get(classNames);
    }

    public static Class getClassByClassName(String className) {
        for (Class classObject : instrumentation.getAllLoadedClasses()) {
            if (classObject.getName().equals(className)) {
                return classObject;
            }
        }
        return null;
    }
}
