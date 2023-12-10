package java.kyodream.handler.memory;


import java.kyodream.handler.struts.StrutsRegistryHandler;

public class StrutsMemory {
    public static void analysts(Object[] strutsObjects) {
        for (Object strutsObject : strutsObjects) {
            StrutsRegistryHandler.AtExit(strutsObject);
        }
    }
}
