package java.kyodream.handler.memory;


import java.kyodream.handler.spring.SpringRegistryHandler;

public class SpringMemory {
    public static void analysts(Object[] springContexts) {
        for (Object springContext : springContexts) {
            SpringRegistryHandler.AtExit(springContext);
        }

    }

//    private static void tryInitSpringContext(Object springContext) {
//        Method initWebApplicationContext = Reflection.getMethodObjectInParentByName(springContext, "initWebApplicationContext");
//        try {
//            initWebApplicationContext.invoke(springContext);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
