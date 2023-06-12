package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.AbstractDataWrapper;
import com.kyodream.debugger.core.thread.DebuggerThread;
import com.kyodream.debugger.service.DebugWebSocket;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mg")
public class ManagerController {

    @Autowired
    private DebugManger bugManger;

    @Autowired
    private DebugWebSocket debugWebSocket;

    @GetMapping("/exist")
    public ApiResponse connectExist() {
        if (bugManger.getDebuggerThread() != null) {
            return Constant.connectExist;
        } else {
            return Constant.connectNoExist;
        }
    }

    @GetMapping("/connect/run")
    public ApiResponse connnecTarget() {
        if (bugManger.getDebuggerThread() != null) {
            return Constant.connectRepeat;
        }
        DebuggerThread debuggerThread = new DebuggerThread(bugManger.getDebuggerArgs(), debugWebSocket, bugManger);
        Thread thread = new Thread(debuggerThread);
        thread.setDaemon(true);
        thread.start();
        return new ApiResponse(200, "操作完成");
    }

    @GetMapping("/clean")
    public ApiResponse cleanData() {
        bugManger.cleanData();
        return new ApiResponse(200, "complete");
    }
}
