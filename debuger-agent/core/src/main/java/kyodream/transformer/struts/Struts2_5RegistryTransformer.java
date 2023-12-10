package kyodream.transformer.struts;

import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.struts.Struts25RegistryInterceptor;
import kyodream.transformer.BaseTransformer;
@ClassName("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")
@TargetMethod("init")
@TargetInterceptor(Struts25RegistryInterceptor.class)
public class Struts2_5RegistryTransformer extends BaseTransformer {
}
