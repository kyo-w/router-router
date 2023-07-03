package com.kyodream.debugger.core;

import com.kyodream.debugger.core.category.*;
import com.kyodream.debugger.core.thread.DebuggerThread;
import com.kyodream.debugger.pojo.DebuggerArgs;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class DebugManger {
    private HashMap<String, DefaultHandlerFramework> handlerOrFramework = new HashMap<>();
    private DebuggerArgs debuggerArgs = null;
    private DebugWebSocket debugWebSocket;

    private VirtualMachine vm;

    private Boolean scannerComplete = true;

    @Autowired
    public DebugManger(SpringMvc spring, Tomcat tomcat, Jetty jetty, Jersey jersey, Struts struts, Filter filter, DebugWebSocket debugWebSocket) {
        handlerOrFramework.put("spring", spring);
        handlerOrFramework.put("tomcat", tomcat);
        handlerOrFramework.put("jetty", jetty);
        handlerOrFramework.put("jersey", jersey);
        handlerOrFramework.put("struts", struts);
        handlerOrFramework.put("filter", filter);
        handlerOrFramework.values().forEach(handleOrPlugin -> {
            handleOrPlugin.setHandleOrFrameworkName();
        });
        this.debugWebSocket = debugWebSocket;
    }

    public void clearAll() {
        Iterator<Map.Entry<String, DefaultHandlerFramework>> iterator = handlerOrFramework.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DefaultHandlerFramework> next = iterator.next();
            next.getValue().clearAny();
            DebuggerThread.clearAllFlag();
        }
    }

    public boolean connectRemoteJVM() {
        if (debuggerArgs == null) {
            return false;
        } else {
            SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
            Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
            argumentHashMap.get("hostname").setValue(debuggerArgs.getHostname());
            argumentHashMap.get("port").setValue(debuggerArgs.getPort());
            argumentHashMap.get("timeout").setValue(debuggerArgs.getTimeout());
            VirtualMachine attach = null;
            try {
                attach = socketAttachingConnector.attach(argumentHashMap);
            } catch (IOException e) {
                debugWebSocket.sendFail("网络连接失败");
            } catch (IllegalConnectorArgumentsException e) {
                debugWebSocket.sendFail("参数格式异常");
            }
            if (attach == null) {
                return false;
            } else {
                this.vm = attach;
            }
            debugWebSocket.sendInfo("连接成功");
            scannerComplete = true;
            return true;
        }
    }

    public boolean startAnalysts() {
        if (this.scannerComplete) {
            DebuggerThread analystsThread = new DebuggerThread(debugWebSocket, this);
            Thread thread = new Thread(analystsThread);
            thread.start();
            return true;
        } else {
            return false;
        }
    }

    public VirtualMachine getVm() {
        return vm;
    }

    public void setScannerComplete(Boolean flag) {
        this.scannerComplete = flag;
    }

    public void stopDebug() {
        this.vm.dispose();
    }

    public HashMap<String, DefaultHandlerFramework> getAllDataWrapper() {
        return handlerOrFramework;
    }

    public boolean existConnect() {
        if (vm == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean closeConnect() {
        if (this.scannerComplete) {
            if(this.vm != null) {
                this.vm.dispose();
                this.vm = null;
            }
            return true;
        }else{
            return false;
        }
    }

    public DebuggerArgs getDebuggerArgs() {
        return debuggerArgs;
    }

    public void setDebuggerArgs(DebuggerArgs debuggerArgs) {
        this.debuggerArgs = debuggerArgs;
    }
}
