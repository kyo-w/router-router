package kyodream.interceptor.jersey;


import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.jersey.JerseyRegistryHandler;

public class JerseyInterceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void AtExit(@Binding.This Object thisObject) {
        JerseyRegistryHandler.AtExit(thisObject);
    }

}
