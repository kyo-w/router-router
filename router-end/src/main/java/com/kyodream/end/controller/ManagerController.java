package com.kyodream.end.controller;

import com.kyodream.end.core.AnalyseStatus;
import com.kyodream.end.core.BugManger;
import com.kyodream.end.core.RepeatConnectError;
import com.kyodream.end.core.category.AbstractDataWrapper;
import com.kyodream.end.utils.ApiResponse;
import com.kyodream.end.utils.Constant;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
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
    private BugManger bugManger;

    @GetMapping("/exist")
    public ApiResponse connectExist() {
        if (bugManger.existConnect()) {
            return Constant.connectExist;
        } else {
            return Constant.connectNoExist;
        }
    }

    @GetMapping("/connect")
    public ApiResponse connnecTarget() {
        try {
            bugManger.connectTarget();
        } catch (IllegalConnectorArgumentsException | IOException e) {
            return Constant.connectError;
        }catch (RepeatConnectError e){
            return Constant.connectRepeat;
        }
        log.info("连接成功");
        return Constant.connectSuccess;
    }

    @GetMapping("/run")
    public ApiResponse runDebug() {
        try {
            bugManger.runDebug();
        } catch (Exception e) {
            return Constant.paramNoSet;
        }
        return Constant.emptyAttr;
    }

    @GetMapping("/analystatus")
    public ApiResponse getAnalyStatus() {
        AnalyseStatus analyseStatus = bugManger.analyseStatus();
        switch (analyseStatus) {
            case NO_ANALYSE:
            case NO_OK:
            default:
                return Constant.anAlyNoComplete;
            case OK:
                return Constant.anAlyComplete;
        }
    }

    @GetMapping("/existtarget")
    public ApiResponse getExistTarget(){
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataStatus();
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        Iterator<Map.Entry<String, AbstractDataWrapper>> iterator = allDataStatus.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, AbstractDataWrapper> next = iterator.next();
            String key = next.getKey();
            AbstractDataWrapper value = next.getValue();
            boolean find = value.isFind();
            stringBooleanHashMap.put(key, find);
        }
        return new ApiResponse(200, stringBooleanHashMap);
    }

    @GetMapping("/stop")
    public ApiResponse stopDebugConnect(){
        bugManger.stopDebug();
        return new ApiResponse(200, "complete");
    }

    @GetMapping("/clean")
    public ApiResponse cleanData(){
        bugManger.cleanData();

        return new ApiResponse(200, "complete");
    }
}
