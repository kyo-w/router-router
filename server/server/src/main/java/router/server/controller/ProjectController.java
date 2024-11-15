package router.server.controller;

import org.springframework.web.bind.annotation.*;
import router.server.entity.ProjectEntity;
import router.server.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @GetMapping("/get/all")
    public ApiResponse getAll() {
        return ApiResponse.status200(projectService.getAllProjects());
    }

    @PostMapping("/creat")
    public ApiResponse create(@RequestBody ProjectEntity project) {
        if (projectService.getProjectByName(project.getAlias()) != null) {
            return ApiResponse.status400("项目已存在");
        } else {
            projectService.createProject(project);
            return ApiResponse.status200("项目创建完成");
        }
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

    @PostMapping("/logout")
    public ApiResponse logout() {
        return projectService.logoutProject();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteProject(@PathVariable Integer id) {
        projectService.deleteProject(id);
        return ApiResponse.Ok();
    }
}
