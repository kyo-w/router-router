package router.server.service;

import lombok.Getter;
import router.server.controller.ProjectCallback;
import router.server.controller.ApiResponse;
import router.server.entity.FrameworkEntity;
import router.server.entity.MiddlewareEntity;
import router.server.entity.ProjectEntity;
import router.server.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    MiddlewareMapper middlewareMapper;
    @Autowired
    FilterMapper filterMapper;

    @Autowired
    ServletMapper servletMapper;

    @Autowired
    FrameworkMapper frameworkMapper;

    @Autowired
    HandlerMapper handlerMapper;

    @Getter
    ProjectEntity currentProjectEntity = null;

    public List<ProjectEntity> getAllProjects() {
        return projectMapper.getAllProjectList();
    }

    public void createProject(ProjectEntity projectEntity) {
        projectMapper.saveProject(projectEntity);
        currentProjectEntity = projectEntity;
    }

    public ProjectEntity getProjectByName(String name) {
        return projectMapper.getProjectByName(name);
    }

    public void completeProject() {
        currentProjectEntity.setComplete(true);
        projectMapper.completeProject(true, currentProjectEntity.getId());
    }


    public boolean selectProject(Integer id) {
        ProjectEntity project = projectMapper.getProjectById(id);
        if (project == null) {
            return false;
        }
        currentProjectEntity = project;
        return true;
    }

    public void deleteProject(Integer id) {
        projectMapper.deleteProjectById(id);
        List<MiddlewareEntity> middlewareEntities = middlewareMapper.selectByProjectId(id);
        middlewareMapper.deleteMiddlewareByProjectId(id);
        for (MiddlewareEntity middlewareEntity : middlewareEntities) {
            filterMapper.deleteByMiddlewareId(middlewareEntity.getId());
            servletMapper.deleteByMiddlewareId(middlewareEntity.getId());
        }
        List<FrameworkEntity> frameworkEntities = frameworkMapper.selectByProjectId(id);
        for (FrameworkEntity frameworkEntity : frameworkEntities) {
            handlerMapper.deleteByFrameworkId(frameworkEntity.getId());
        }
    }

    public ApiResponse checkProject(ProjectCallback callback) {
        if (currentProjectEntity == null) {
            return ApiResponse.status400("还未选择指定项目");
        }
        return callback.callback(currentProjectEntity);
    }

    public ApiResponse logoutProject() {
        currentProjectEntity = null;
        return ApiResponse.Ok();
    }
}
