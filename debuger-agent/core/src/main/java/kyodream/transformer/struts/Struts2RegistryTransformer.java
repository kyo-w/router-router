package kyodream.transformer.struts;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.struts.Struts2RegistryInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter")
@TargetMethod("init")
@TargetInterceptor(Struts2RegistryInterceptor.class)
public class Struts2RegistryTransformer extends BaseTransformer {
}
