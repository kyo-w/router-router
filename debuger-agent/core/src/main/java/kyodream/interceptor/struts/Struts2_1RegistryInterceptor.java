package kyodream.interceptor.struts;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.struts.Struts2_1RegistryHandler;

public class Struts2_1RegistryInterceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void atExit(@Binding.This Object thisObject) {
        Struts2_1RegistryHandler.AtExit(thisObject);
    }
}
