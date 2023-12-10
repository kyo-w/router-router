package kyodream.interceptor.jetty;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.jetty.JettyRegistryHandler;

public class JettyInterceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void AtExit(@Binding.This Object thisObject){
        JettyRegistryHandler.atExit(thisObject);
    }
}
