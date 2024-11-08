package router.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {

    @RequestMapping("/get")
    public ApiResponse getLogData(@RequestParam("type") String type, @RequestParam("limit") Integer limit, @RequestParam("page") Integer page) {
        return ApiResponse.Ok();
    }

    @RequestMapping("/clean")
    public ApiResponse clearLogData() {
        return ApiResponse.Ok();
    }
}
