package java.kyodream.handler.memory;


import java.kyodream.handler.jersey.JerseyRegistryHandler;

public class JerseyMemory {
    public static void analysts(Object[] jerseyObjects) {
        for (Object jerseyObject : jerseyObjects) {
            JerseyRegistryHandler.AtExit(jerseyObject);
        }
    }
}
