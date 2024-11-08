package router.server.service;

import lombok.Getter;
import router.server.controller.ProjectCallback;
import router.server.controller.ApiResponse;
import router.server.entity.ProjectEntity;
import router.server.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectMapper projectMapper;

    @Getter
    ProjectEntity currentProjectEntity = null;

    public ProjectEntity getProjectByName(String alias) {
        return projectMapper.getProjectByName(alias);
    }

    public List<ProjectEntity> getAllProjects() {
        return projectMapper.getAllProjectList();
    }

    public void saveProject(ProjectEntity projectEntity) {
        projectMapper.saveProject(projectEntity);
    }


    public boolean selectProject(Integer id) {
        ProjectEntity project = projectMapper.getProjectById(id);
        if (project == null){
            return false;
        }
        currentProjectEntity = project;
        return true;
    }

    public ApiResponse checkProject(ProjectCallback callback) {
        if (currentProjectEntity == null){
            return ApiResponse.status400("还未选择指定项目");
        }
        return callback.callback(currentProjectEntity);
    }
}
