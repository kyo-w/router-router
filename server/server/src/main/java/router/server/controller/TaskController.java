package router.server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;
import router.server.service.ConnectService;
import router.server.service.ProjectService;
import router.server.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ConnectService connectService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/complete")
    public ApiResponse isComplete() {
        return projectService.checkProject(p -> {
            return projectService.getCurrentProjectEntity().isComplete() ? ApiResponse.Ok() : ApiResponse.Fail();
        });
    }

    @PostMapping("/start")
    public ApiResponse startTask() {
        return projectService.checkProject(p -> {
            if (connectService.getCurrentVirtualMachine() == null) {
                return ApiResponse.status400("你需要连接JDWP再启用调试任务");
            }
            if (projectService.getCurrentProjectEntity().isComplete()) {
                return ApiResponse.status400("已经分析过一次了!");
            }
            return taskService.startAnalysts(connectService.getCurrentVirtualMachine());
        });
    }

    @GetMapping("/status")
    public ApiResponse getStatus() {
        return projectService.checkProject(p -> taskService.statusAnalysts());
    }

    @DeleteMapping("/stop")
    public ApiResponse stopTask() {
        return projectService.checkProject(p -> taskService.stopAnalysts());
    }
}
