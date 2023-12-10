package kyodream.interceptor.jetty;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.jetty.JettyHttpStreamHandler;

public class JettyHttpStreamInterceptor {
    @AtEnter(inline = true, suppress = Exception.class)
    public static void AtEnter(@Binding.This Object servletHolderObject, @Binding.Args Object[] args){
        JettyHttpStreamHandler.AtEnter(servletHolderObject,args);
    }

    @AtExit(inline = true, suppress = Exception.class)
    public static void AtExit(){
        JettyHttpStreamHandler.AtExit();
    }
}
