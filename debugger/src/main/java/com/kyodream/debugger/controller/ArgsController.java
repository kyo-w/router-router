package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.thread.DebuggerThread;
import com.kyodream.debugger.pojo.DebugType;
import com.kyodream.debugger.pojo.DebuggerArgs;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/args")
public class ArgsController {

    @Autowired
    private DebugManger bugManger;

    @PostMapping("/set")
    public ApiResponse SetArgs(@RequestBody DebuggerArgs args) {
        if(bugManger.getDebuggerThread() != null){
            return Constant.hasExist;
        }
        if(args.getTimeout() == null){
            args.setTimeout("3000");
        }
        bugManger.setDebuggerArgs(args);
        return Constant.settingSuccess;
    }

    @GetMapping("/get")
    public ApiResponse getSetting() {
        DebuggerArgs debuggerArgs = bugManger.getDebuggerArgs();
        if(debuggerArgs != null){
            return new ApiResponse(200, debuggerArgs);
        }
        else{
            return Constant.emptyAttr;
        }
    }

    @GetMapping("/clean")
    public ApiResponse cleanSetting(){
        bugManger.setDebuggerArgs(null);
        return Constant.handleSuccess;
    }
}

