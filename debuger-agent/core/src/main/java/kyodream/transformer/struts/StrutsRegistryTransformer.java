package kyodream.transformer.struts;

import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.struts.StrutsRegistryInterceptor;
import kyodream.transformer.BaseTransformer;

@ClassName("org.apache.struts.action.ActionServlet")
@TargetMethod("init")
@TargetInterceptor(StrutsRegistryInterceptor.class)
public class StrutsRegistryTransformer extends BaseTransformer {

}
