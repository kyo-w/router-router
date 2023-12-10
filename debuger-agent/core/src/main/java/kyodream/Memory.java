package kyodream;

import java.kyodream.handler.memory.*;
import java.util.Set;

public class Memory {
    public static void loadMemoryObject() {
        analystsMiddleware();
    }

    private static void analystsMiddleware() {
        Set<Class> mapperClass = AgentManagerImpl.getClasses("org.apache.catalina.mapper.Mapper");
        Set<Class> webAppContextClasses = AgentManagerImpl.getClasses("org.eclipse.jetty.webapp.WebAppContext");
        Set<Class> dispatcherServletClasses = AgentManagerImpl.getClasses("org.springframework.web.servlet.DispatcherServlet");
        Set<Class> jerseyClasses = AgentManagerImpl.getClasses("com.sun.jersey.spi.container.servlet.ServletContainer");
        Set<Class> jersey2Classes = AgentManagerImpl.getClasses("org.glassfish.jersey.servlet.ServletContainer");

        Set<Class> struts1 = AgentManagerImpl.getClasses("org.apache.struts.action.ActionServlet");
        Set<Class> struts22 = AgentManagerImpl.getClasses("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter");
        Set<Class> struts25 = AgentManagerImpl.getClasses("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter");
        Set<Class> struts21 = AgentManagerImpl.getClasses("org.apache.struts2.dispatcher.FilterDispatcher");

        if (mapperClass != null) {
            for (Class classObject : mapperClass) {
                Object[] mapperObject = VmUtils.getInstanceByClassName(classObject);
                TomcatMemory.analysts(mapperObject);
            }
        }
        if (webAppContextClasses != null) {
            System.out.println("jetty111");
            for (Class classObject : webAppContextClasses) {
                Object[] webAppContextObject = VmUtils.getInstanceByClassName(classObject);
                JettyMemory.analysts(webAppContextObject);
            }
        }
        if (dispatcherServletClasses != null) {
            for (Class classObject : dispatcherServletClasses) {
                Object[] dispatcherServletObject = VmUtils.getInstanceByClassName(classObject);
                SpringMemory.analysts(dispatcherServletObject);
            }
        }
        if (jerseyClasses != null) {
            for (Class classObject : jerseyClasses) {
                Object[] jerseyObjects = VmUtils.getInstanceByClassName(classObject);
                JerseyMemory.analysts(jerseyObjects);
            }
        }

        if (jersey2Classes != null) {
            for (Class classObject : jersey2Classes) {
                Object[] jersey2Objects = VmUtils.getInstanceByClassName(classObject);
                Jersey2Memory.analysts(jersey2Objects);
            }
        }
        if (struts1 != null) {
            for (Class classObject : struts1) {
                Object[] struts1Object = VmUtils.getInstanceByClassName(classObject);
                StrutsMemory.analysts(struts1Object);
            }
        }
        if (struts22 != null) {
            for (Class classObject : struts22) {
                Object[] struts22Object = VmUtils.getInstanceByClassName(classObject);
                Struts2Memory.analysts(struts22Object);
            }
        }
        if (struts21 != null) {
            for (Class classObject : struts21) {
                Object[] struts21Object = VmUtils.getInstanceByClassName(classObject);
                Struts2_1Memory.analysts(struts21Object);
            }
        }
        if (struts25 != null) {
            for (Class classObject : struts25) {
                Object[] struts25Object = VmUtils.getInstanceByClassName(classObject);
                Struts2_5Memory.analysts(struts25Object);
            }
        }
    }
}
