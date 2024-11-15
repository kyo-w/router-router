package router.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import router.server.mapper.*;

import javax.servlet.ServletContext;


@Component
public class DataBaseConfig implements ServletContextAware {
    @Autowired
    FilterMapper filterMapper;
    @Autowired
    FrameworkMapper frameworkMapper;
    @Autowired
    HandlerMapper handlerMapper;
    @Autowired
    MiddlewareMapper middlewareMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    ServletMapper servletMapper;

    @Override
    public void setServletContext(ServletContext servletContext) {
        filterMapper.initTable();
        frameworkMapper.initTable();
        handlerMapper.initTable();
        middlewareMapper.initTable();
        projectMapper.initTable();
        servletMapper.initTable();
    }
}
