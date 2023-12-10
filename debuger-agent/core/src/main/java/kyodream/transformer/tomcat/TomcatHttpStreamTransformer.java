package kyodream.transformer.tomcat;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.tomcat.TomcatHttpStreamInterceptor;
import kyodream.transformer.BaseTransformer;

/**
 * Hook Http所有请求流/过滤器
 */
@ClassName("org.apache.catalina.core.ApplicationFilterChain")
@TargetMethod("internalDoFilter")
@TargetInterceptor(TomcatHttpStreamInterceptor.class)
public class TomcatHttpStreamTransformer extends BaseTransformer {

    @Override
    public void afterCreate(ClassLoader loader, byte[] newClassByteCode) {
    }
}