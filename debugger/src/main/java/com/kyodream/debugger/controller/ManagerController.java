package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mg")
public class ManagerController {

    @Autowired
    private DebugManger bugManger;

    @GetMapping("/exist")
    public ApiResponse connectExist() {
        if (bugManger.existConnect()) {
            return Constant.connectExist;
        } else {
            return Constant.connectNoExist;
        }
    }

    @GetMapping("/connect")
    public ApiResponse connectTarget() {
        if (bugManger.existConnect()) {
            return Constant.connectRepeat;
        }
        if (bugManger.connectRemoteJVM()) {
            return new ApiResponse(200, "连接成功");
        } else {
            return new ApiResponse(400, "连接失败");
        }
    }

    @GetMapping("/run")
    public ApiResponse analysts() {
        if (bugManger.startAnalysts()) {
            return new ApiResponse(200, "操作完成");
        } else {
            return new ApiResponse(200, "已存在分析线程");
        }
    }

    @GetMapping("/clean")
    public ApiResponse cleanData() {
        bugManger.clearAll();
        return new ApiResponse(200, "complete");
    }

    @GetMapping("/close/connect")
    public ApiResponse closeConnect() {
        boolean completeClose = bugManger.closeConnect();
        return new ApiResponse(200, completeClose);
    }
}
