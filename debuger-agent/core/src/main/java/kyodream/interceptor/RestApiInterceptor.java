package kyodream.interceptor;


import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;

import java.kyodream.handler.common.TraceHandler;

public class RestApiInterceptor {
    @AtEnter(inline = true, suppress = RuntimeException.class)
    public static void atEnter() {
        TraceHandler.atEnter();
    }

}
