package com.kyodream.end.core.category;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventSet;

import java.util.Set;

public interface Handle extends DataWrapper {
    //    每一个中间件或者框架都提供一个处理接口
//    Each middleware or framework provides a processing interface
    void handleEvent(EventSet eventSet, VirtualMachine attach);

    //    每一个中间件或者框架都必须提供一个断点类
//    Each middleware or framework must provide a breakpoint class
    Set<String> getFilterClassName();

}
