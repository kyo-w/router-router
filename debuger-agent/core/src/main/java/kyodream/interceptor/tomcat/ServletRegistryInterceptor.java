package kyodream.interceptor.tomcat;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;

import java.kyodream.handler.common.ServletRegistryHandler;

public class ServletRegistryInterceptor {
    @AtEnter(inline = true, suppress = RuntimeException.class)
    public static void atEnter(@Binding.Args Object[] args) {
        ServletRegistryHandler.atEnter(args);
    }
}
