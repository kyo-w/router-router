package com.kyodream.end.controller;

import com.kyodream.end.core.BugManger;
import com.kyodream.end.pojo.Args;
import com.kyodream.end.utils.ApiResponse;
import com.kyodream.end.utils.Constant;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/args")
public class ArgsController {

    @Autowired
    private BugManger bugManger;

    @PostMapping("/set")
    public ApiResponse SetArgs(@RequestBody Args args) {
        if (bugManger.existConnect()) {
            return Constant.hasExist;
        }
        bugManger.settingArg(args);
        return Constant.settingSuccess;
    }

    @GetMapping("/get")
    public ApiResponse getSetting() {
        Args currentArgs = bugManger.getCurrentArgs();
        if(currentArgs != null){
            return new ApiResponse(200, currentArgs);
        }
        else{
            return Constant.emptyAttr;
        }
    }

    @GetMapping("/clean")
    public ApiResponse cleanSetting(){
        bugManger.cleanArg();
        return Constant.handleSuccess;
    }
}

