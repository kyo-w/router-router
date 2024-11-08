package router.server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import router.server.service.ConnectService;
import router.server.service.ProjectService;
import router.server.service.TaskService;

@RestController("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ConnectService connectService;

    @Autowired
    private ProjectService projectService;

    @PostMapping("/start")
    public ApiResponse startTask() {
        return projectService.checkProject(p -> {
            if (connectService.getCurrentVirtualMachine() == null) {
                return ApiResponse.status400("你需要连接JDWP再启用调试任务");
            }
            return taskService.startAnalysts(connectService.getCurrentVirtualMachine());
        });
    }

    @DeleteMapping("/stop")
    public ApiResponse stopTask() {
        return projectService.checkProject(p -> taskService.stopAnalysts());
    }
}
