package com.kyodream.debugger.core;

import com.kyodream.debugger.core.category.*;
import com.kyodream.debugger.core.thread.DebuggerThread;
import com.kyodream.debugger.pojo.DebuggerArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DebugManger {
    private HashMap<String, AbstractDataWrapper> dataWrappers = new HashMap<>();

    private DebuggerThread DebuggerThread = null;

    private DebuggerArgs debuggerArgs = null;

    @Autowired
    public DebugManger(Spring spring, Tomcat tomcat, Jetty jetty, Jersey jersey, Struts struts) {

        dataWrappers.put("spring", spring);
        dataWrappers.put("tomcat", tomcat);
        dataWrappers.put("jetty", jetty);
        dataWrappers.put("jersey", jersey);
        dataWrappers.put("struts", struts);
    }

    public void cleanData() {
        Iterator<Map.Entry<String, AbstractDataWrapper>> iterator = dataWrappers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AbstractDataWrapper> next = iterator.next();
            next.getValue().clearData();
        }
    }

    public void stopDebug() {
        this.DebuggerThread.stopDebug();
    }

    public HashMap<String, AbstractDataWrapper> getAllData() {
        return dataWrappers;
    }

    public com.kyodream.debugger.core.thread.DebuggerThread getDebuggerThread() {
        return DebuggerThread;
    }

    public void setDebuggerThread(com.kyodream.debugger.core.thread.DebuggerThread debuggerThread) {
        DebuggerThread = debuggerThread;
    }

    public DebuggerArgs getDebuggerArgs() {
        return debuggerArgs;
    }

    public void setDebuggerArgs(DebuggerArgs debuggerArgs) {
        this.debuggerArgs = debuggerArgs;
    }
}
