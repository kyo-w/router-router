package com.kyodream.debugger.controller;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.pojo.ClassObject;
import com.kyodream.debugger.utils.ApiResponse;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/memory")
public class MemoryController {

    @Autowired
    DebugManger debugManger;

    public boolean checkEnv() {
        return debugManger.getVirtualMachine() != null;
    }

    public boolean canGetInstance() {
        if (!checkEnv()) {
            return false;
        }
        return debugManger.getVirtualMachine().canGetInstanceInfo();
    }

    @GetMapping("/ct/{className}")
    public ApiResponse getClassByName(@PathVariable("className") String className) {

        Object[] result = debugManger.getVirtualMachine().classesByName(className).stream().map(elem -> {
            ClassType classType = (ClassType) elem;
            ClassLoaderReference classLoaderReference = classType.classLoader();
            if (classLoaderReference == null) {
                return new ClassObject(classType.name(), "null", 0);
            }
            return new ClassObject(classType.name(), classLoaderReference.type().name(), classType.classLoader().uniqueID());
        }).toArray();
        return ApiResponse.ok(result);
    }


    @GetMapping("/obj/{className}")
    public ApiResponse getClassObjectByName(@PathVariable("className") String className) {
        if (!canGetInstance()) {
            return ApiResponse.fail("无法操作");
        }
        if (debugManger.getVirtualMachine() == null) {
            return ApiResponse.fail("空操作");
        }
        ReferenceType[] array = debugManger.getVirtualMachine().classesByName(className).stream().toArray(ReferenceType[]::new);
        ArrayList<ObjectReference> result = new ArrayList<>();
        for (ReferenceType referenceType : array) {
            List<ObjectReference> instances = referenceType.instances(0);
            result.addAll(instances);
        }
        return ApiResponse.ok(result);
    }
}
