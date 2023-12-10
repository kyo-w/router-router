package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.pojo.DebuggerArgs;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import com.sun.jdi.VirtualMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@RestController
@RequestMapping("/mg")
public class ManagerController {

    @Autowired
    private DebugManger bugManger;


    @GetMapping("/connect/exist")
    public ApiResponse connectExist() {
        if (bugManger.existConnect()) {
            return Constant.connectExist;
        } else {
            return Constant.connectNoExist;
        }
    }

    @PostMapping("/connect/open")
    public ApiResponse connectTarget(@RequestBody DebuggerArgs args) {
        if (bugManger.existConnect()) {
            return Constant.connectRepeat;
        }
        if (bugManger.connectRemoteJVM(args)) {
            return new ApiResponse(200, "连接成功");
        } else {
            return new ApiResponse(400, "连接失败");
        }
    }

    @GetMapping("/connect/close")
    public ApiResponse closeConnect() {
        bugManger.closeConnect();
        return new ApiResponse(200, null);
    }

    @PostMapping("/args/set")
    public ApiResponse setArgs(@RequestBody DebuggerArgs args) {
        Socket socket = new Socket();
        boolean isConnected = false;
        String message = null;
        try {
            socket.connect(new InetSocketAddress(args.getHostname(), Integer.parseInt(args.getPort())), 3000);
            isConnected = socket.isConnected();
        } catch (Exception e) {
            message = e.getMessage();
        }
        if (isConnected) {
            return new ApiResponse(200, null);
        }
        return new ApiResponse(400, message);
    }


    @GetMapping("/task/run")
    public ApiResponse startTask() {
        if (bugManger.startTask()) {
            return new ApiResponse(200, "操作完成");
        } else {
            return new ApiResponse(300, "已存在分析线程");
        }
    }
    @GetMapping("/task/stop")
    public ApiResponse stopTask() {
        if (bugManger.stopTask()) {
            return new ApiResponse(200, "操作完成");
        } else {
            return new ApiResponse(300, "已存在分析线程");
        }
    }

    @GetMapping("/task/exist")
    public ApiResponse taskExist() {
        if (bugManger.taskExist()) {
            return new ApiResponse(200, "存在分析线程");
        } else {
            return new ApiResponse(300, "不存在分析线程");
        }
    }

}
