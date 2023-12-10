package kyodream.interceptor.jersey;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;

import java.kyodream.handler.jersey.Jersey2RegistryHandler;

public class Jersey2Interceptor {
    @AtExit(inline = true, suppress = RuntimeException.class)
    public static void AtExit(@Binding.This Object thisObject) {
        Jersey2RegistryHandler.AtExit(thisObject);
    }
}
