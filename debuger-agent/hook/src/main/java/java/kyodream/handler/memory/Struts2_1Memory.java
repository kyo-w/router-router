package java.kyodream.handler.memory;


import java.kyodream.handler.struts.Struts2_1RegistryHandler;

public class Struts2_1Memory {
    public static void analysts(Object[] strutsObjects) {
        for (Object strutsObject : strutsObjects) {
            Struts2_1RegistryHandler.AtExit(strutsObject);
        }
    }
}
