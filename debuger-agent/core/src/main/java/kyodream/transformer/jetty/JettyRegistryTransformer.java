package kyodream.transformer.jetty;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.jetty.JettyInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.eclipse.jetty.webapp.WebAppContext")
@TargetMethod("startContext")
@TargetInterceptor(JettyInterceptor.class)
public class JettyRegistryTransformer extends BaseTransformer {
}
