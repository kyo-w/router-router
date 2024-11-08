package router.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import router.server.service.ProgressService;
import router.server.service.ProjectService;

/**
 * progress特定Task任务中的某个阶段性过程
 */
@RestController("/progress")
public class ProgressController {
    @Autowired
    ProjectService projectService;
    @Autowired
    ProgressService progressService;

    @GetMapping("/getall")
    public ApiResponse progressTaskList() {
        return projectService.checkProject(p ->
                ApiResponse.status200(progressService.getCurrentTasks()));
    }
}
