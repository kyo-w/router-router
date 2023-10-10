package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.thread.StackThread;
import com.kyodream.debugger.service.StackWebSocket;
import com.kyodream.debugger.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stack")
public class StackController {
    @Autowired
    public DebugManger debugManger;

    @Autowired
    private StackWebSocket stackWebSocket;

    private StackThread stackThread;


    @RequestMapping("/if/ready")
    public ApiResponse canGo() {
        return (debugManger.completeAnalysts ? new ApiResponse(200, true) : new ApiResponse(200, false));
    }

    @RequestMapping("/get/cache")
    public ApiResponse getCacheData(){
        return new ApiResponse(200, debugManger.allStack);
    }

    @RequestMapping("/clean/cache")
    public ApiResponse cleanCache(){
        debugManger.allStack.clear();
        return new ApiResponse(200, null);
    }

    @RequestMapping("/start/listen")
    public ApiResponse listen() {
        if(debugManger.vm == null){
            return new ApiResponse(400, "未连接任意目标");
        }
        if(!debugManger.completeAnalysts){
            return new ApiResponse(300, "堆栈监听对象来自内存扫描之后的数据，请先完成内存扫描!");
        }
        if (stackThread == null) {
            stackThread = new StackThread(debugManger, stackWebSocket);
            Thread thread = new Thread(stackThread);
            thread.start();
            return new ApiResponse(200, "开启监听线程");
        }
        return new ApiResponse(400, "已存在监听线程");
    }

    @RequestMapping("/end/listen")
    public ApiResponse endListen() {
        if (stackThread != null) {
            stackThread.endListen();
            stackThread = null;
        }
        return new ApiResponse(200, null);
    }

}
