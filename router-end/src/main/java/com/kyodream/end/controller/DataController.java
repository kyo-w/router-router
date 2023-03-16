package com.kyodream.end.controller;

import com.kyodream.end.core.AnalyseStatus;
import com.kyodream.end.core.BugManger;
import com.kyodream.end.core.category.*;
import com.kyodream.end.utils.ApiResponse;
import com.kyodream.end.utils.Constant;
import com.kyodream.end.utils.ExportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private Tomcat tomcat;

    @Autowired
    private Jetty jetty;

    @Autowired
    private Spring spring;

    @Autowired
    private JerseyHandle jerseyHandle;

    @Autowired
    private Struts struts;

    @Autowired
    private BugManger bugManger;

    @GetMapping("/{middle}")
    public ApiResponse getMiddleData(@PathVariable("middle") String middle) {
        if (middle.equals("tomcat")) {
            return new ApiResponse(200, tomcat.getDataWrapper());
        } else if (middle.equals("jetty")) {
            return new ApiResponse(200, jetty.getDataWrapper());
        } else if (middle.equals("spring")) {
            return new ApiResponse(200, spring.getDataWrapper());
        } else if (middle.equals("jersey")) {
            return new ApiResponse(200, jerseyHandle.getDataWrapper());
        } else if (middle.equals("struts")) {
            return new ApiResponse(200, struts.getDataWrapper());
        } else {
            return Constant.unknownParam;
        }
    }

    @GetMapping("/exist")
    public ApiResponse exist() {
        AnalyseStatus analyseStatus = bugManger.analyseStatus();
        switch (analyseStatus) {
            case OK:
                return Constant.existData;
            default:
                return Constant.emptyData;
        }
    }

    @GetMapping("/exist/{target}")
    public ApiResponse existTarget(@PathVariable("target") String target) {
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataStatus();
        AbstractDataWrapper abstractDataWrapper = allDataStatus.get(target);
        return new ApiResponse(200, abstractDataWrapper.isFind());
    }

    @GetMapping("/version/{target}")
    public ApiResponse getVersion(@PathVariable("target") String target) {
        HashMap<String, AbstractDataWrapper> allDataStatus = bugManger.getAllDataStatus();
        AbstractDataWrapper abstractDataWrapper = allDataStatus.get(target);
        return  new ApiResponse(200, abstractDataWrapper.getVersion());
    }

    @GetMapping("/count/{target}")
    public ApiResponse getTargetCount(@PathVariable("target") String middle){
        if (middle.equals("tomcat")) {
            return new ApiResponse(200, tomcat.getDataWrapper().size());
        } else if (middle.equals("jetty")) {
            return new ApiResponse(200, jetty.getDataWrapper().size());
        } else if (middle.equals("spring")) {
            return new ApiResponse(200, spring.getDataWrapper().size());
        } else if (middle.equals("jersey")) {
            return new ApiResponse(200, jerseyHandle.getDataWrapper().size());
        } else if (middle.equals("struts")) {
            return new ApiResponse(200, struts.getDataWrapper().size());
        } else {
            return Constant.unknownParam;
        }
    }

    @GetMapping("/export/{target}")
    public void exportData(HttpServletResponse response, @PathVariable("target") String middle){
        if(middle.equals("all")){
            ExportUtils.exportAllData(bugManger.getAllDataStatus(), response);
        }
    }

    @GetMapping("/struts/module")
    public ApiResponse StrutsMouleName(){
        return new ApiResponse(200, bugManger.getStrutsModuleName());
    }
}
