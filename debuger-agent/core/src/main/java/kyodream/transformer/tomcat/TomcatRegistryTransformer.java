package kyodream.transformer.tomcat;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.tomcat.TomcatRegistryInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.apache.catalina.mapper.Mapper")
@TargetMethod("addContextVersion")
@TargetInterceptor(TomcatRegistryInterceptor.class)
public class TomcatRegistryTransformer extends BaseTransformer {
}
