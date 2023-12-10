package kyodream.map;

public enum DebugClass {
    TOMCAT_9("org.apache.catalina.mapper.Mapper"),
    JETTY_WEB("org.eclipse.jetty.servlet.ServletContextHandler"),
    TOMCAT_7_Deprecated("org.apache.tomcat.util.http.mapper.Mapper"),
    STRUTS_22("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter"),
    STRUTS_25("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter"),
    SPRING("org.springframework.web.servlet.DispatcherServlet"),

    JERSEY_1("org.glassfish.jersey.servlet.ServletContainer"),
    TOMCAT_STACK_REQUEST("org.apache.catalina.core.StandardWrapperValve"),
    TOMCAT_STACK_FILTER("org.apache.catalina.core.ApplicationFilterChain"),
    JERSEY_2("com.sun.jersey.spi.container.servlet.ServletContainer");


    private String className;

    DebugClass(String name) {
        this.className = name;
    }

    public String className() {
        return className;
    }
}
