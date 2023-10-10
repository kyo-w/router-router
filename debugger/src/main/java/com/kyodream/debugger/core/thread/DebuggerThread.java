package com.kyodream.debugger.core.thread;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.DefaultFramework;
import com.kyodream.debugger.core.category.DefaultHandler;
import com.kyodream.debugger.core.category.DefaultHandlerFramework;
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

    private Set<DefaultHandler> handles = new HashSet<>();

    private static HashMap<DefaultHandler, AnalystsObject> handlerAnalystsObject = new HashMap<>();

    public DebuggerThread(DebugManger debugManger) {
        this.webSocket = debugManger.debugWebSocket;
        this.debugManger = debugManger;
        this.vm = debugManger.vm;
        initHandler();
    }

    @Override
    public void run() {
        debugManger.scanner = true;
        try {
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
            initHandlerFlag();
            webSocket.sendInfo("完成扫描^-^");
//        开始分析
            handleEvent();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            debugManger.scanner = false;
        }
        debugManger.completeAnalysts = true;
        webSocket.sendSuccess("完成扫描");
    }

    private void initHandler() {
        debugManger.handlerOrFramework.values().forEach(handleOrFramework -> {
            if (handleOrFramework instanceof DefaultHandler) {
                this.handles.add((DefaultHandler) handleOrFramework);
            }
        });
    }

    private void initHandlerFlag() {
        handlerAnalystsObject.forEach((handler, analystsObject) -> {
            if (analystsObject.getStatus() == AnalystsObjectStatus.HasChange) {
                handler.clearData();
            }
        });
    }

    private void scannerMemory() {
        this.handles.forEach(handler -> {
            Set<String> allClassName = handler.getDiscoveryClass();
            allClassName.forEach(discoveryClass -> {
                List<ReferenceType> referenceTypes = vm.classesByName(discoveryClass);
                for (ReferenceType referenceType : referenceTypes) {
                    List<ObjectReference> instances = referenceType.instances(0);
                    HashSet<ObjectReference> objectReferences = new HashSet<>(instances);
                    if(objectReferences.size() != 0) {
                        handler.addAnalystsObjectSet(objectReferences);
                        judgeHandleExistNewObject(handler, objectReferences);
                    }
                }
            });
        });
    }

    private void judgeHandleExistNewObject(DefaultHandler handle, HashSet<ObjectReference> instanceSet) {
        if (handlerAnalystsObject.get(handle) == null) {
            handlerAnalystsObject.put(handle, new AnalystsObject(instanceSet.size(), AnalystsObjectStatus.Init));
        } else {
            if (handlerAnalystsObject.get(handle).getSize() != instanceSet.size()) {
                handlerAnalystsObject.get(handle).setSize(instanceSet.size());
                handlerAnalystsObject.get(handle).setStatus(AnalystsObjectStatus.HasChange);
            } else {
                handlerAnalystsObject.get(handle).setStatus(AnalystsObjectStatus.NotChange);
            }
        }
    }

    private void handleEvent() {
        for (DefaultHandler handle : handlerAnalystsObject.keySet()) {
            if (!judgeHandleIfChangeOrInit(handle)) {
                webSocket.sendInfo("当前内存中：" + handle.getName() + "无新的路由对象");
                continue;
            }
            try {
                handle.startAnalysts(vm);
            } catch (Exception e) {
                e.printStackTrace();
                webSocket.sendFail(handle.getName() + "处理过程中出现无法处理的异常!");
            }
        }
    }

    private boolean judgeHandleIfChangeOrInit(DefaultHandler handler) {
        AnalystsObjectStatus status = handlerAnalystsObject.get(handler).getStatus();
        if (status == AnalystsObjectStatus.HasChange || status == AnalystsObjectStatus.Init) {
            return true;
        } else {
            return false;
        }
    }

    public static void clearAllFlag(){
        handlerAnalystsObject.values().forEach(statusObject->{
            statusObject.setSize(0);
        });
    }
}

class AnalystsObject {
    private Integer size;
    private AnalystsObjectStatus status;

    public AnalystsObject(Integer size, AnalystsObjectStatus status) {
        this.size = size;
        this.status = status;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public AnalystsObjectStatus getStatus() {
        return status;
    }

    public void setStatus(AnalystsObjectStatus status) {
        this.status = status;
    }
}

enum AnalystsObjectStatus {
    Init,
    NotChange,
    HasChange
}