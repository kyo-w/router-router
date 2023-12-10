package kyodream.transformer.jersey;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.jersey.JerseyInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("com.sun.jersey.spi.container.servlet.ServletContainer")
@TargetMethod("init;;()V")
@TargetInterceptor(JerseyInterceptor.class)
public class JerseyRegistryTransformer extends BaseTransformer {

}
