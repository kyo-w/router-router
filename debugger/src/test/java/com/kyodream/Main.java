package com.kyodream;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyodream.debugger.DebuggerApplication;
import com.kyodream.debugger.pojo.ApiStack;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) throws JsonProcessingException {
        LinkedHashMap<String, String> objectObjectLinkedHashMap = new LinkedHashMap<>();
        objectObjectLinkedHashMap.put("321", "321");
        String test = objectMapper.writeValueAsString(new ApiStack("test", objectObjectLinkedHashMap));
        System.out.printf(test);
    }
}
