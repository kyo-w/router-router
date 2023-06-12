package com.kyodream.debugger.core.thread;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.AbstractDataWrapper;
import com.kyodream.debugger.pojo.DebuggerArgs;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.tools.jdi.SocketAttachingConnector;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class DebuggerThread implements Runnable {

    private volatile boolean stopFlag = false;

    private DebuggerArgs debuggerArgs;
    private DebugWebSocket webSocket;
    private DebugManger debugManger;

    private VirtualMachine vm;

    private Set<AbstractDataWrapper> handles = new HashSet<>();

    public DebuggerThread(DebuggerArgs debuggerArgs, DebugWebSocket debugWebSocket, DebugManger debugManger) {
        this.debuggerArgs = debuggerArgs;
        this.webSocket = debugWebSocket;
        this.debugManger = debugManger;
    }

    @Override
    public void run() {
        log.info("创建新线程");
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue(debuggerArgs.getHostname());
        argumentHashMap.get("port").setValue(debuggerArgs.getPort());
        argumentHashMap.get("timeout").setValue(debuggerArgs.getTimeout());
        VirtualMachine attach = null;
        try {
            attach = socketAttachingConnector.attach(argumentHashMap);
        } catch (IOException e) {
            webSocket.sendFail("网络连接失败");
        } catch (IllegalConnectorArgumentsException e) {
            webSocket.sendFail("参数格式异常");
        }
        if (attach == null) {
            return;
        }
        log.info("连接成功");
        debugManger.setDebuggerThread(this);
        this.vm = attach;
//        遍历内存Class对象
        webSocket.sendInfo("扫描内存对象中...");
        scannerMemory();
        webSocket.sendInfo("完成扫描^-^");

//        设置断点
        webSocket.sendInfo("设置断点");
        setFilterClass();
        webSocket.sendInfo("动态等待调试信息，实时更新内存变化");
        EventQueue eventQueue = vm.eventQueue();
        EventSet events = null;
        while (true && !stopFlag) {
            try {
                if (!((events = eventQueue.remove()) != null)) {
                    continue;
                }
            } catch (InterruptedException e) {
                webSocket.sendFail("等待断点异常");
            }
            scannerMemory();
            handleEvent(events);
        }
    }

    private void setFilterClass() {
        EventRequestManager eventRequestManager = vm.eventRequestManager();
        for (AbstractDataWrapper handle : handles) {
            Set<String> discoveryClass = handle.getDiscoveryClass();
            for (String className : discoveryClass) {
                MethodEntryRequest methodEntryRequest = eventRequestManager.createMethodEntryRequest();
                methodEntryRequest.addClassFilter(className);
                methodEntryRequest.enable();
            }
        }
    }

    private void scannerMemory() {
        HashMap<String, AbstractDataWrapper> allData = debugManger.getAllData();
        allData.values().forEach(handleOrPlugin -> {
            if(handleOrPlugin.isFind()){
                return;
            }
//            获取插件或者中间件的每一个发现对象类
            Set<String> discoveryClass = handleOrPlugin.getDiscoveryClass();
            discoveryClass.forEach((String className) -> {
//                从VM中获取发现对象类
                List<ReferenceType> referenceTypes = vm.classesByName(className);
                referenceTypes.forEach((ReferenceType referenceType) -> {
                    List<ObjectReference> instances = referenceType.instances(0);
                    HashSet<ObjectReference> instanceSet = new HashSet<>(instances);
//                    注册添加
                    handleOrPlugin.addAnalysisObject(instanceSet);
                    handles.add(handleOrPlugin);
                });
            });
        });

    }

    private void handleEvent(EventSet events) {
        for (Event event : events) {
            if (event instanceof MethodEntryEvent) {
                for (AbstractDataWrapper handle : handles) {
                    if (!handle.isFind()) {
                        handle.analystsObject(vm);
                    }
                }
            }
            vm.resume();
        }
    }

    public void stopDebug() {
        this.vm.dispose();
    }
}