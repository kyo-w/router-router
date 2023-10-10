package com.kyodream.debugger.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiStack {
    private String name;
    private LinkedHashMap<String, String> stacks;

    public void pushStack(String className, String methodName) {
        stacks.put(className, methodName);
    }

}