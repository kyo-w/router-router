package kyodream.transformer.struts;

import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.struts.Struts2_1RegistryInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.apache.struts2.dispatcher.FilterDispatcher")
@TargetMethod("init")
@TargetInterceptor(Struts2_1RegistryInterceptor.class)
public class Struts2_1RegistryTransformer extends BaseTransformer {
}
