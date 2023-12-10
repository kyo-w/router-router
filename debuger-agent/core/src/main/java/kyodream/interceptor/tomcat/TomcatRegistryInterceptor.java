package kyodream.interceptor.tomcat;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;

import java.kyodream.handler.tomcat.TomcatRegistryHandler;

/**
 * 插桩:org.apache.catalina.mapper.Mapper.addContextVersion(...)
 */
public class TomcatRegistryInterceptor {
    @AtEnter(inline = true, suppress = RuntimeException.class)
    public static void atEnter(@Binding.This Object object, @Binding.Args Object[] args) {
        TomcatRegistryHandler.atEnter(object, args);
    }
}
