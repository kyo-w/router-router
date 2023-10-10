package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import com.sun.jdi.VirtualMachine;
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

    /**
     * 确定当前是否已经存在一个JDI的连接，但并不说明已经分析过
     * @return
     */
    @GetMapping("/exist")
    public ApiResponse connectExist() {
        if (bugManger.vm != null) {
            return Constant.connectExist;
        } else {
            return Constant.connectNoExist;
        }
    }

    /**
     * 连接目标的JDI端口
     * @return
     */
    @GetMapping("/connect")
    public ApiResponse connectTarget() {
        if (bugManger.vm != null) {
            return Constant.connectRepeat;
        }
        if (bugManger.connectRemoteJVM()) {
            return new ApiResponse(200, "连接成功");
        } else {
            return new ApiResponse(400, "连接失败");
        }
    }

    /**
     * 目标已经存在连接时，开始分析目标
     * @return
     */
    @GetMapping("/run")
    public ApiResponse analysts() {
        if (bugManger.startAnalysts()) {
            return new ApiResponse(200, "操作完成");
        } else {
            return new ApiResponse(200, "已存在分析线程");
        }
    }

    /**
     * 清除所有的连接标记/数据
     * @return
     */
    @GetMapping("/clean")
    public ApiResponse cleanData() {
        bugManger.clearAll();
        return new ApiResponse(200, "complete");
    }

    /**
     * 关闭连接
     * @return
     */
    @GetMapping("/close/connect")
    public ApiResponse closeConnect() {
        if(bugManger.vm != null){
            bugManger.vm.dispose();
            bugManger.vm = null;
        }
        return new ApiResponse(200, bugManger.scanner);
    }
}
