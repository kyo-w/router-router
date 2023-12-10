package kyodream.transformer;


import kyodream.annotation.ClassName;
import kyodream.annotation.TargetInterceptor;
import kyodream.annotation.TargetMethod;
import kyodream.interceptor.tomcat.ServletRegistryInterceptor;

/**
 * 产品自定义添加Servlet
 */
@ClassName("org.apache.catalina.core.ApplicationContext")
@TargetMethod("addServlet;;(Ljava/lang/String;Ljava/lang/String;Ljavax/servlet/Servlet;Ljava/util/Map;)Ljavax/servlet/ServletRegistration$Dynamic;")
@TargetInterceptor(ServletRegistryInterceptor.class)
public class ServletRegistryTransformer extends BaseTransformer {
}