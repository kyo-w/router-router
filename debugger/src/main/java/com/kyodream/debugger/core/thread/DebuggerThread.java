package com.kyodream.debugger.core.thread;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.AbstractDataWrapper;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DebuggerThread implements Runnable {
    private DebugWebSocket webSocket;
    private DebugManger debugManger;

    private VirtualMachine vm;

    private Set<AbstractDataWrapper> handles = new HashSet<>();

    public DebuggerThread(DebugWebSocket debugWebSocket, DebugManger debugManger) {
        this.webSocket = debugWebSocket;
        this.debugManger = debugManger;
        this.vm = debugManger.getVm();
    }

    @Override
    public void run() {
        webSocket.sendInfo("清理或初始化数据");
        clearData();
        debugManger.setScannerComplete(false);
        log.info("创建分析线程");
//        遍历内存Class对象
        webSocket.sendInfo("扫描内存对象中...");
        if (!vm.canGetInstanceInfo()) {
            webSocket.sendFail("目标不支持内存访问");
            return;
        } else {
            webSocket.sendInfo("目标支持内存访问");
        }
        scannerMemory();
        webSocket.sendInfo("完成扫描^-^");
//        开始分析
        handleEvent();
        debugManger.setScannerComplete(true);
        webSocket.sendSuccess("完成扫描");
    }

    private void clearData() {
        HashMap<String, AbstractDataWrapper> allDataWrapper = debugManger.getAllDataWrapper();
        allDataWrapper.values().forEach(abstractDataWrapper -> {
            abstractDataWrapper.clearData();
        });
    }

    private void scannerMemory() {
        HashMap<String, AbstractDataWrapper> allData = debugManger.getAllDataWrapper();
        allData.values().forEach(handleOrPlugin -> {
            if (handleOrPlugin.isFind()) {
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

    private void handleEvent() {
        for (AbstractDataWrapper handle : handles) {
            if (!handle.isFind()) {
                try {
                    handle.analystsObject(vm);
                } catch (Exception e) {
                    e.printStackTrace();
                    webSocket.sendFail(handle.handleOrPlugin + "处理过程中出现无法处理的异常!");
                }
            }
        }
    }
}