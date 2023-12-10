package kyodream.transformer.spring;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.spring.SpringRegistryInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.springframework.web.servlet.DispatcherServlet")
@TargetMethod("initHandlerMappings")
@TargetInterceptor(SpringRegistryInterceptor.class)
public class SpringMvcRegistry extends BaseTransformer {
}
