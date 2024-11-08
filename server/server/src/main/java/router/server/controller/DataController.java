package router.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import router.server.context.DatabaseContext;
import router.server.mapper.FilterMapper;
import router.server.mapper.MiddlewareMapper;
import router.server.mapper.ProjectMapper;
import router.server.service.ProjectService;
import router.type.HandlerType;
import router.type.MiddlewareType;

import java.util.*;

/**
 * 数据获取类
 */
@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    DatabaseContext context;

    @Autowired
    MiddlewareMapper middlewareMapper;

    @Autowired
    FilterMapper filterMapper;

    @Autowired
    ProjectService projectService;

    @GetMapping("/middleware/getall")
    public ApiResponse getAllMiddlewares() {
        return projectService.checkProject((p) ->
                ApiResponse.status200(context.getAllMiddleware(p.getId())));
    }


    @GetMapping("/middleware/{target}/panel")
    public ApiResponse getSpecifiedMiddleware(@PathVariable("target") MiddlewareType type) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getMiddlewarePanelByType(p.getId(), new MiddlewareType[]{type})));
    }

    @GetMapping("/middleware/{middleid}/filter")
    public ApiResponse getSpecifiedMiddlewareFilter(@PathVariable("middleid") Integer middleid, @RequestParam("limit") Integer limit,
                                                    @RequestParam("page") Integer page) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getMiddlewareFilter(middleid, page, limit)));
    }

    @GetMapping("/middleware/{middleid}/filter/count")
    public ApiResponse getSpecifiedMiddlewareFilterCount(@PathVariable("middleid") Integer middleid) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getMiddlewareFilterCount(middleid)));
    }

    @GetMapping("/middleware/{middleid}/urlmap")
    public ApiResponse getSpecifiedMiddlewareUrlMap(@PathVariable("middleid") Integer middleid, @RequestParam("limit") Integer limit,
                                                    @RequestParam("page") Integer page) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getMiddlewareServlet(middleid, page, limit)));
    }

    @GetMapping("/middleware/{middleid}/urlmap/count")
    public ApiResponse getSpecifiedMiddlewareUrlMapCount(@PathVariable("middleid") Integer middleid) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getMiddlewareServletCount(middleid)));
    }

    @GetMapping("/framework/getall")
    public ApiResponse getAllFrameworks() {
        return projectService.checkProject((p) ->
                ApiResponse.status200(context.getFrameworkAllData(p.getId())));
    }

    @GetMapping("/framework/{target}/panel")
    public ApiResponse getSpecifiedFrameworkPanel(@PathVariable("target") HandlerType type) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getSpecifiedFrameworkPanel(p.getId(), new HandlerType[]{type})));
    }

    @GetMapping("/framework/{frameworkid}/handlerMap")
    public ApiResponse getSpecifiedFrameworkHandlerMap(@PathVariable("frameworkid") Integer frameworkId, @RequestParam("limit") Integer limit,
                                                       @RequestParam("page") Integer page) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getFrameworkHandler(frameworkId, page, limit)));
    }

    @GetMapping("/framework/{frameworkid}/handlerMap/count")
    public ApiResponse getSpecifiedFrameworkHandlerMapCount(@PathVariable("frameworkid") Integer frameworkId) {
        return projectService.checkProject(p ->
                ApiResponse.status200(context.getFrameworkHandlerCount(frameworkId)));
    }
}
