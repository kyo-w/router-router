package java.kyodream.handler.memory;


import java.kyodream.handler.jersey.Jersey2RegistryHandler;

public class Jersey2Memory {
    public static void analysts(Object[] jersey2Objects) {
        for (Object jersey2Object : jersey2Objects) {
            Jersey2RegistryHandler.AtExit(jersey2Object);
        }
    }
}
