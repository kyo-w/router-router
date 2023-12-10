package kyodream.interceptor.tomcat;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.tomcat.TomcatHttpStreamHandler;

/**
 * 插桩： internalDoFilter
 */
public class TomcatHttpStreamInterceptor {

    @AtEnter(inline = true, suppress = RuntimeException.class)
    public static void atEnter( @Binding.This Object object, @Binding.Args Object[] args) {
        TomcatHttpStreamHandler.atEnter(object, args);
    }
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void atExit() {
        TomcatHttpStreamHandler.atExit();
    }
}
