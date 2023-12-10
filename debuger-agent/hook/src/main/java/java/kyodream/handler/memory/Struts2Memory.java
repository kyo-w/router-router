package java.kyodream.handler.memory;


import java.kyodream.handler.struts.Struts2RegistryHandler;

public class Struts2Memory {
    public static void analysts(Object[] strutsObjects) {
        for (Object strutsObject : strutsObjects) {
            Struts2RegistryHandler.AtExit(strutsObject, false);
        }
    }
}
