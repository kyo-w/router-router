package kyodream.interceptor.jetty;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.jetty.JettyFilterStreamHandler;

public class JettyFilterStreamInterceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void AtExit(@Binding.Return Object chainObject) {
        JettyFilterStreamHandler.AtExit(chainObject);
    }
}
