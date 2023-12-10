package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.utils.ApiResponse;
import kyodream.map.AnalystsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 数据获取类
 */
@RestController
@RequestMapping("/data")
public class DataController {
    private final static HashMap<String, AnalystsType[]> alias = new HashMap<>() {{
        put("tomcat", new AnalystsType[]{AnalystsType.TOMCAT});
        put("jetty", new AnalystsType[]{AnalystsType.JETTY_WEB});
        put("struts", new AnalystsType[]{AnalystsType.STRUTS_1, AnalystsType.STRUTS_2});
        put("jersey", new AnalystsType[]{AnalystsType.JERSEY_1, AnalystsType.JERSEY_2});
        put("spring", new AnalystsType[]{AnalystsType.SPRING});
    }};
    @Autowired
    private DebugManger bugManger;
    private HashMap<String, String> versionMap = new HashMap<>();

    @GetMapping("/middle")
    public ApiResponse getMiddlewareData() {
        List<Object> data = bugManger.getData(AnalystsType.TOMCAT, AnalystsType.JETTY_WEB);
        if (data != null) {
            return new ApiResponse(200, data);
        }
        return new ApiResponse(200, null);
    }

    @GetMapping("/middle/exist")
    public ApiResponse existMiddle() {
        List<AnalystsType> data = bugManger.existDataType(AnalystsType.TOMCAT, AnalystsType.JETTY_WEB);
        if (data != null) {
            return new ApiResponse(200, data);
        }
        return new ApiResponse(200, null);
    }

    @GetMapping("/framework")
    public ApiResponse getFrameworkData() {
        List<Object> data = bugManger.getData(AnalystsType.JERSEY_1, AnalystsType.JERSEY_2,
                AnalystsType.STRUTS_2, AnalystsType.STRUTS_1, AnalystsType.SPRING);
        if (data != null) {
            return new ApiResponse(200, data);
        }
        return new ApiResponse(200, null);
    }

    @GetMapping("/framework/exist")
    public ApiResponse existFramework() {
        List<AnalystsType> data = bugManger.existDataType(AnalystsType.JERSEY_1, AnalystsType.JERSEY_2,
                AnalystsType.STRUTS_2, AnalystsType.STRUTS_1, AnalystsType.SPRING);
        if (data != null) {
            return new ApiResponse(200, data);
        }
        return new ApiResponse(200, null);
    }


    @GetMapping("/{target}/{index}")
    public ApiResponse getTargetData(@PathVariable("target") String target, @PathVariable("index") Integer index) {
        AnalystsType[] analystsTypes = alias.get(target);
        List<Object> data = bugManger.getData(analystsTypes);
        Object result = data.get(index - 1);
        if (target != null) {
            return new ApiResponse(200, result);
        }
        return new ApiResponse(200, null);
    }

    @GetMapping("/count/{target}")
    public ApiResponse getTargetCount(@PathVariable("target") String target) {
        AnalystsType[] analystsTypes = alias.get(target);
        int size = bugManger.getData(analystsTypes).size();
        return new ApiResponse(200, size);
    }


    @GetMapping("/exist/failservlet")
    public ApiResponse existFailServlet() {
        if (!bugManger.getFailServlet().isEmpty()) {
            return new ApiResponse(200, true);
        }
        return new ApiResponse(200, false);
    }

    @GetMapping("/failservlet")
    public ApiResponse getFailServlet() {
        HashSet<String> failServlet = bugManger.getFailServlet();
        return new ApiResponse(200, failServlet);
    }
}
