package router.server.controller;

import org.springframework.web.bind.annotation.PathVariable;
import router.server.entity.ProjectEntity;
import router.server.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @GetMapping("/get/all")
    public ApiResponse getAll() {
        return ApiResponse.status200(projectService.getAllProjects());
    }

    @PostMapping("/creat")
    public ApiResponse create(ProjectEntity project) {
        return projectService.checkProject((p) -> {
            projectService.saveProject(project);
            return ApiResponse.Ok();
        });
    }


    @PostMapping("/select/{id}")
    public ApiResponse selectProject(@PathVariable Integer id) {
        return projectService.selectProject(id) ? ApiResponse.Ok() : ApiResponse.Fail();
    }

    @GetMapping("/current")
    public ApiResponse getCurrentProject() {
        ProjectEntity currentProjectEntity = projectService.getCurrentProjectEntity();
        return currentProjectEntity != null ? ApiResponse.status200(currentProjectEntity) : ApiResponse.Fail();
    }
}
