package router.pipeline;

import router.analysts.IAnalysts;
import router.analysts.MapAnalysts;
import router.context.Context;
import router.handler.*;
import router.parse.UrlParse;
import router.type.MiddlewareType;

public class ServletPipeLine {
    public static IAnalysts initServlet(MiddlewareType type, IAnalysts servletWrapperRef, IAnalysts classLoader)  {
        switch (type) {
            case Tomcat:
                try {
                    IAnalysts currentThreadClassLoader = servletWrapperRef.getCurrentThreadClassLoader();
                    servletWrapperRef.changeThreadClassLoader(classLoader);
                    IAnalysts result = servletWrapperRef.invokeMethod("allocate");
                    servletWrapperRef.invokeMethod("unload");
                    servletWrapperRef.changeThreadClassLoader(currentThreadClassLoader);
                    return result;
                }catch (Exception e){
                    return null;
                }
            case Jetty:
                try {
                    //            jetty9暂时不涉及上下文加载器的问题
                    return servletWrapperRef.invokeMethod("initServlet");
                }catch (Exception e){
                    return null;
                }
            default:
                return null;
        }
    }

    public static void handlerServlet(UrlParse urlParse, IAnalysts servletRef, IAnalysts attributeRef, Context context)throws Exception  {
        if (servletRef.isInstanceof("org.apache.struts.action.ActionServlet")) {
            handlerStruts1(urlParse, attributeRef, context);
        } else if (servletRef.isInstanceof("org.springframework.web.servlet.DispatcherServlet")) {
            new SpringMvcHandler().handler(urlParse, servletRef, context);
        } else if (servletRef.isInstanceof("org.glassfish.jersey.servlet.ServletContainer")) {
            new Jersey2Handler().handler(urlParse, servletRef, context);
        } else if (servletRef.isInstanceof("com.sun.jersey.spi.container.servlet.ServletContainer")) {
            new Jersey1Handler().handler(urlParse, servletRef, context);
        }
    }

    public static void handlerStruts1(UrlParse urlParse, IAnalysts attributeRef, Context context) throws Exception {
        MapAnalysts.Entry[] mapInstanceRefs = new MapAnalysts(attributeRef).getKV();
        for (MapAnalysts.Entry mapInstanceRef : mapInstanceRefs) {
            if (mapInstanceRef.getKey().convertToString().startsWith("org.apache.struts.action.MODULE")) {
                new Struts1Handler().handler(urlParse, mapInstanceRef.getValue(), context);
            }
        }
    }
}
