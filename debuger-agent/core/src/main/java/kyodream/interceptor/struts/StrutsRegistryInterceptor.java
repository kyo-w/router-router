package kyodream.interceptor.struts;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.struts.StrutsRegistryHandler;

public class StrutsRegistryInterceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void atExit(@Binding.This Object thisObject) {
        StrutsRegistryHandler.AtExit(thisObject);
    }
}
