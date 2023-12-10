package kyodream.transformer.jersey;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.jersey.Jersey2Interceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.glassfish.jersey.servlet.ServletContainer")
@TargetMethod("init;;()V")
@TargetInterceptor(Jersey2Interceptor.class)
public class Jersey2RegistryTransformer extends BaseTransformer {
}
