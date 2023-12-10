package kyodream.transformer.jetty;

import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.jetty.JettyFilterStreamInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.eclipse.jetty.servlet.ServletHandler")
@TargetMethod("getFilterChain")
@TargetInterceptor(JettyFilterStreamInterceptor.class)
public class JettyFilterStreamTransformer extends BaseTransformer {
}
