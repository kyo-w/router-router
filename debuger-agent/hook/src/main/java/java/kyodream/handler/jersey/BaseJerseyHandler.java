package java.kyodream.handler.jersey;


import java.kyodream.handler.FrameworkHandler;
import java.kyodream.record.JerseyHandlerChain;
import java.kyodream.utils.Reflection;
import java.kyodream.utils.VirtualPathUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseJerseyHandler extends FrameworkHandler {

    public static Object[] analystsClass(String virtualPath, Collection<Object> cachedClasses) {
        List<JerseyHandlerChain> jerseyHandlerChainList = new ArrayList<>();
        for (Object rawObject : cachedClasses) {
            Class classObject = (Class) rawObject;
            String topUrl = null;
            String className = classObject.getName();
            Annotation[] annotations = classObject.getAnnotations();
            // 类上的@Path需要进行注册
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getName().equals("javax.ws.rs.Path")) {
                    topUrl = (String) Reflection.getAnnotationFieldObjectByName("value", annotation);
                    String url = VirtualPathUtils.fillPath(virtualPath, topUrl);
                    JerseyHandlerChain jerseyHandlerChain = new JerseyHandlerChain();
                    jerseyHandlerChain.setUrl(url);
                    jerseyHandlerChain.setHandlerClass(className);
                    jerseyHandlerChainList.add(jerseyHandlerChain);
                }
            }

            // 方法上的@Path需要进行注册
            Method[] declaredMethods = classObject.getDeclaredMethods();
            for (Method method : declaredMethods) {
                Annotation[] methodAnnotations = method.getAnnotations();
                for (Annotation annotation : methodAnnotations) {
                    if (annotation.annotationType().getName().equals("javax.ws.rs.Path")) {
                        String subUrl = (String) Reflection.getAnnotationFieldObjectByName("value", annotation);
                        String url = VirtualPathUtils.fillPath(virtualPath, VirtualPathUtils.concatPath(topUrl, subUrl));
                        JerseyHandlerChain jerseyHandlerChain = new JerseyHandlerChain();
                        jerseyHandlerChain.setUrl(url);
                        jerseyHandlerChain.setHandlerClass(className);
                        jerseyHandlerChainList.add(jerseyHandlerChain);
                    }
                }
            }
        }
        return jerseyHandlerChainList.toArray();
    }
}
