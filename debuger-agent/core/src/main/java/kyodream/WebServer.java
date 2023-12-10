package kyodream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;

public class WebServer {
    public static void initServer(HashMap<String, String> args) {
        Server server = new Server(9090);
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase("jar:" + Server.class.getResource("/static").getFile());
        webAppContext.setInitParameter("spring.liveBeansView.mbeanDomain", "routerouter");
        server.setHandler(webAppContext);


        URL resource = WebServer.class.getClassLoader().getResource("web/springmvc-servlet.xml");
        String file = resource.getFile().replace("\\", "/");
        XmlWebApplicationContext mvcContext = new XmlWebApplicationContext();
        mvcContext.setConfigLocation("jar:" + file);

        ServletHolder servletHolder = new ServletHolder("spring", new DispatcherServlet(mvcContext));
        servletHolder.setInitParameter("spring.liveBeansView.mbeanDomain", "routerouter");
        webAppContext.addServlet(servletHolder, "/api/*");

        FilterHolder filterHolder = webAppContext.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD"); // 允许的请求方法
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin"); // 允许的请求头
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
