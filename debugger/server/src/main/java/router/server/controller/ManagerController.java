package router.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import router.server.service.ConnectService;
import router.server.service.ProjectService;

@Slf4j
@RestController
@RequestMapping("/jdwp")
public class ManagerController {

    @Autowired
    ProjectService projectService;

    @Autowired
    ConnectService connectService;

    @GetMapping("/testConnect")
    public ApiResponse connectTest() {
        return projectService.checkProject((p)->
                connectService.testConnect(projectService.getCurrentProjectEntity()));
    }

    @PostMapping("/connect")
    public ApiResponse connectJVM() {
        return projectService.checkProject((p)->
                connectService.connectJDWPWithProject(projectService.getCurrentProjectEntity()));
    }

    @DeleteMapping("/close")
    public ApiResponse deleteProject() {
        return projectService.checkProject((p)->connectService.closeConnect());
    }
}
