package kyodream.interceptor.spring;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.spring.SpringRegistryHandler;

public class SpringRegistryInterceptor {

    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void atExit(@Binding.This Object thisObject){
        SpringRegistryHandler.AtExit(thisObject);
    }
}
