package router.server.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import router.analysts.IAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.mapping.MiddlewareMapping;
import router.publish.IPublish;
import router.server.entity.*;
import router.server.mapper.*;
import router.server.publish.WsPublish;
import router.server.service.ProjectService;
import router.type.HandlerType;
import router.type.MiddlewareType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseContext implements Context {

    @Autowired
    private ProjectService projectService;
    @Autowired
    MiddlewareMapper middlewareMapper;
    @Autowired
    ServletMapper servletMapper;
    @Autowired
    FilterMapper filterMapper;
    @Autowired
    FrameworkMapper frameworkMapper;
    @Autowired
    HandlerMapper handlerMapper;

    @Autowired
    WsPublish wsPublish;

    private HashMap<Long, IAnalysts> analystsCache = new HashMap<>();

    private boolean completeTask = false;

    public List<String> getAllMiddleware(Integer projectId) {
        return middlewareMapper.selectTypeByProjectId(projectId);
    }

    public List<MiddlewareEntity> getMiddlewarePanelByType(Integer projectId, MiddlewareType type) {
        return middlewareMapper.selectByProjectIdAndType(projectId, type.name());
    }

    public List<FrameworkEntity> getSpecifiedFrameworkPanel(Integer projectId, HandlerType type) {
        return frameworkMapper.selectByProjectIdAndType(projectId, type.name());
    }

    public List<String> getFrameworkAllData(Integer id) {
        return frameworkMapper.selectTypeByProjectId(id);
    }

    public List<FilterEntity> getMiddlewareFilter(Integer middleId, Integer page, Integer limit) {
        return filterMapper.getFilterByMiddleId(middleId, page, limit);
    }

    public Integer getMiddlewareFilterCount(Integer middleId) {
        return filterMapper.selectFilterCountByMiddleId(middleId);
    }

    public List<ServletEntity> getMiddlewareServlet(Integer middleId, Integer page, Integer limit) {
        return servletMapper.getServletByMiddlewareId(middleId, page, limit);
    }

    public Integer getMiddlewareServletCount(Integer middleId) {
        return servletMapper.selectServletCountByMiddlewareId(middleId);
    }

    public List<HandlerEntity> getFrameworkHandler(Integer frameworkId, Integer page, Integer limit) {
        return handlerMapper.selectAllByFrameworkId(frameworkId, page, limit);
    }

    public Integer getFrameworkHandlerCount(Integer frameworkId) {
        return handlerMapper.selectCountByFrameworkId(frameworkId);
    }

    @Override
    public void pushMiddleware(MiddlewareMapping middleware) {
        MiddlewareEntity middlewareEntity = new MiddlewareEntity();
        middlewareEntity.setType(middleware.getType().toString());
        middlewareEntity.setVersion(middleware.getVersion());
        middlewareEntity.setVirtualPath(middleware.getVirtualPath());
        middlewareEntity.setPhysicalPath(middleware.getPhysicalPath());
        middlewareEntity.setProjectId(projectService.getCurrentProjectEntity().getId());
        middlewareMapper.insert(middlewareEntity);
        List<ServletEntity> servlets = middleware.getServletMap().stream().map(e -> {
            ServletEntity servletEntity = new ServletEntity();
            servletEntity.setClassname(e.getClassname());
            servletEntity.setMiddleId(middlewareEntity.getId());
            servletEntity.setUrls(e.getPath());
            servletEntity.setMark(false);
            return servletEntity;
        }).collect(Collectors.toList());
        servletMapper.insertServlets(servlets);
        List<FilterEntity> filters = middleware.getFilterMap().stream().map(f -> {
            FilterEntity filterEntity = new FilterEntity();
            filterEntity.setUrl(f.getUrlPath());
            filterEntity.setPriority(f.getPriority());
            filterEntity.setClassname(f.getClassName());
            filterEntity.setMark(false);
            filterEntity.setMiddleId(middlewareEntity.getId());
            return filterEntity;
        }).collect(Collectors.toList());
        filterMapper.insertFilters(filters);
    }

    @Override
    public void pushFramework(FrameworkMapping framework) {
        FrameworkEntity frameworkEntity = new FrameworkEntity();
        frameworkEntity.setProjectId(projectService.getCurrentProjectEntity().getId());
        frameworkEntity.setVersion(framework.getVersion());
        frameworkEntity.setType(framework.getType().toString());
        frameworkEntity.setContextPath(framework.getContextPath());
        frameworkMapper.insertFramework(frameworkEntity);
        List<HandlerEntity> urlMap = framework.getUrlMap().stream().map(e -> {
            HandlerEntity handlerEntity = new HandlerEntity();
            handlerEntity.setFrameworkId(frameworkEntity.getId());
            handlerEntity.setClassname(e.getClassname());
            handlerEntity.setUrls(e.getPath());
            handlerEntity.setMark(false);
            return handlerEntity;
        }).collect(Collectors.toList());
        handlerMapper.insertHandler(urlMap);
    }

    @Override
    public void completeTask() {
        projectService.completeProject();
    }
    @Override
    public IPublish getPublish() {
        return wsPublish;
    }

    @Override
    public void pushAnalysts(IAnalysts analysts) {
        analystsCache.put(analysts.getId(), analysts);
    }

    @Override
    public IAnalysts getAnalystsByUniqId(Long id) {
        return analystsCache.remove(id);
    }
}
