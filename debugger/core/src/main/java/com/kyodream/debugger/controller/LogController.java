package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.service.AbstractWS;
import com.kyodream.debugger.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/log")

public class LogController {
    @Autowired
    private DebugManger bugManger;

    @RequestMapping("/get")
    public ApiResponse getLogData() {
        HashSet<AbstractWS.AnalystsInfo> logData = bugManger.getLogData();
        return ApiResponse.ok(logData);
    }

    @RequestMapping("/clean")
    public ApiResponse clearLogData() {
        bugManger.cleanLog();
        return ApiResponse.ok(null);
    }
}
