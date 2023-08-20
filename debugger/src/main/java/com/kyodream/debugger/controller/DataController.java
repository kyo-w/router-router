package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.*;
import com.kyodream.debugger.utils.ApiResponse;
import com.kyodream.debugger.utils.Constant;
import com.kyodream.debugger.utils.ExportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DebugManger bugManger;
    /**
     *
     * @param middle
     * @return
     */
    @GetMapping("/{middle}")
    public ApiResponse getMiddleData(@PathVariable("middle") String middle) {
        HandlerFrameworkAncestor target = bugManger.getHandlerOrFrameworkByName(middle);
        if(target != null){
            return new ApiResponse(200, target.getDataWrapper());
        }else{
            return Constant.unknownParam;
        }
    }

    @GetMapping("/modify/spring")
    public ApiResponse springModify() {
        SpringMvc spring = (SpringMvc) bugManger.getHandlerOrFrameworkByName("spring");
        if(spring.getModify()){
            return new ApiResponse(200, true);
        }else{
            return new ApiResponse(200, false);
        }
    }

    @GetMapping("/modify/spring/prefix")
    public ApiResponse springPrefix() {
        SpringMvc spring = (SpringMvc) bugManger.getHandlerOrFrameworkByName("spring");
        return new ApiResponse(200, spring.getPrefix());
    }

    @GetMapping("/exist/{target}")
    public ApiResponse existTarget(@PathVariable("target") String targetName) {
        HandlerFrameworkAncestor targetObject = bugManger.getHandlerOrFrameworkByName(targetName);
        return new ApiResponse(200, targetObject.ifCompleteAnalysts());
    }

    @GetMapping("/version/{target}")
    public ApiResponse getVersion(@PathVariable("target") String targetName) {
        HandlerFrameworkAncestor targetObject = bugManger.getHandlerOrFrameworkByName(targetName);
        return new ApiResponse(200, targetObject.getVersion());
    }

    @GetMapping("/count/{target}")
    public ApiResponse getTargetCount(@PathVariable("target") String middle) {
        if (middle.equals("tomcat")) {
            HandlerFrameworkAncestor targetObject = bugManger.getHandlerOrFrameworkByName(middle);
            return new ApiResponse(200, targetObject.getDataWrapper().size());
        } else {
            return Constant.unknownParam;
        }
    }

    @GetMapping("/exist/SurvivalObject")
    public ApiResponse getExistTarget() {
        HashMap<String, DefaultHandlerFramework> allDataStatus = bugManger.getAllDataWrapper();
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        Iterator<Map.Entry<String, DefaultHandlerFramework>> iterator = allDataStatus.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DefaultHandlerFramework> next = iterator.next();
            String key = next.getKey();
            DefaultHandlerFramework value = next.getValue();
            boolean find = value.ifFind();
            stringBooleanHashMap.put(key, find);
        }
        HandlerFrameworkAncestor filter = bugManger.getHandlerOrFrameworkByName("filter");
        if(filter.ifFind()){
            stringBooleanHashMap.put("filter", true);
        }else{
            stringBooleanHashMap.put("filter", false);
        }
        return new ApiResponse(200, stringBooleanHashMap);
    }

    @GetMapping("/export/{target}")
    public void exportData(HttpServletResponse response, @PathVariable("target") String middle) {
        if (middle.equals("all")) {
            ExportUtils.exportAllData(bugManger.getAllDataWrapper(), response);
        }
    }

    @GetMapping("/filter")
    public ApiResponse getFilterMap(){
        Filter filter = (Filter) bugManger.getHandlerOrFrameworkByName("filter");
        HashMap<String, LinkedHashMap<String, LinkedHashSet<String>>> filterMap = filter.getFilterMap();
        return new ApiResponse(200, filterMap);
    }
}
