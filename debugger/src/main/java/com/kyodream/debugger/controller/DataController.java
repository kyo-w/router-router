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
    private Tomcat tomcat;

    @Autowired
    private Jetty jetty;

    @Autowired
    private SpringMvc spring;

    @Autowired
    private Jersey jersey;

    @Autowired
    private Struts struts;

    @Autowired
    private DebugManger bugManger;

    @Autowired
    private Filter filter;

    @GetMapping("/{middle}")
    public ApiResponse getMiddleData(@PathVariable("middle") String middle) {
        if (middle.equals("tomcat")) {
            return new ApiResponse(200, tomcat.getDataWrapper());
        } else if (middle.equals("jetty")) {
            return new ApiResponse(200, jetty.getDataWrapper());
        } else if (middle.equals("spring")) {
            return new ApiResponse(200, spring.getDataWrapper());
        } else if (middle.equals("jersey")) {
            return new ApiResponse(200, jersey.getDataWrapper());
        } else if (middle.equals("struts")) {
            return new ApiResponse(200, struts.getDataWrapper());
        } else {
            return Constant.unknownParam;
        }
    }

    @GetMapping("/modify/spring")
    public ApiResponse springModify() {
        HashMap<String, AbstractDataWrapper> allDataWrapper = bugManger.getAllDataWrapper();
        SpringMvc spring = (SpringMvc)allDataWrapper.get("spring");
        if(spring.getModify()){
            return new ApiResponse(200, true);
        }else{
            return new ApiResponse(200, false);
        }
    }

    @GetMapping("/modify/spring/prefix")
    public ApiResponse springPrefix() {
        HashMap<String, AbstractDataWrapper> allDataWrapper = bugManger.getAllDataWrapper();
        SpringMvc spring = (SpringMvc)allDataWrapper.get("spring");
        return new ApiResponse(200, spring.getPrefix());
    }

    @GetMapping("/exist/{target}")
    public ApiResponse existTarget(@PathVariable("target") String target) {
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataWrapper();
        AbstractDataWrapper abstractDataWrapper = allDataStatus.get(target);
        return new ApiResponse(200, abstractDataWrapper.isCompleteAnalysts());
    }

    @GetMapping("/version/{target}")
    public ApiResponse getVersion(@PathVariable("target") String target) {
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataWrapper();
        AbstractDataWrapper abstractDataWrapper = allDataStatus.get(target);
        return new ApiResponse(200, abstractDataWrapper.getVersion());
    }

    @GetMapping("/count/{target}")
    public ApiResponse getTargetCount(@PathVariable("target") String middle) {
        if (middle.equals("tomcat")) {
            return new ApiResponse(200, tomcat.getDataWrapper().size());
        } else if (middle.equals("jetty")) {
            return new ApiResponse(200, jetty.getDataWrapper().size());
        } else if (middle.equals("spring")) {
            return new ApiResponse(200, spring.getDataWrapper().size());
        } else if (middle.equals("jersey")) {
            return new ApiResponse(200, jersey.getDataWrapper().size());
        } else if (middle.equals("struts")) {
            return new ApiResponse(200, struts.getDataWrapper().size());
        } else {
            return Constant.unknownParam;
        }
    }

    @GetMapping("/existtarget")
    public ApiResponse getExistTarget() {
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataWrapper();
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        Iterator<Map.Entry<String, AbstractDataWrapper>> iterator = allDataStatus.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AbstractDataWrapper> next = iterator.next();
            String key = next.getKey();
            AbstractDataWrapper value = next.getValue();
            boolean find = value.isFind();
            stringBooleanHashMap.put(key, find);
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
        HashMap<String, LinkedHashMap<String, Set<String>>> filterMap = filter.getFilterMap();
        return new ApiResponse(200, filterMap);
    }
}
