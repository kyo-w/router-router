package kyodream.transformer.jetty;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.jetty.JettyHttpStreamInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.eclipse.jetty.servlet.ServletHolder")
@TargetMethod("handle")
@TargetInterceptor(JettyHttpStreamInterceptor.class)
public class JettyHttpStreamTransformer extends BaseTransformer {

}
