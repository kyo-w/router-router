package com.kyodream.debugger.core.thread;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.category.DefaultHandlerFramework;
import com.kyodream.debugger.pojo.ApiStack;
import com.kyodream.debugger.service.DebugWebSocket;
import com.kyodream.debugger.service.StackWebSocket;
import com.sun.jdi.*;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.MethodEntryRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * StackThread所有的监控类均来自DebuggerThread的分析结果
 */
@Slf4j
public class StackThread implements Runnable {
    private DebugManger debugManger;

    private boolean stop = false;

    private List<MethodEntryRequest> methodCache = new ArrayList<>();
    private StackWebSocket stackWebSocket;

    public StackThread(DebugManger debugManger, StackWebSocket stackWebSocket) {
        this.debugManger = debugManger;
        this.stackWebSocket = stackWebSocket;
    }

    public void createMethodPoint(String className) {
        MethodEntryRequest methodEntryRequest = debugManger.vm.eventRequestManager().createMethodEntryRequest();
        methodEntryRequest.addClassFilter(className);
        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        methodEntryRequest.enable();
        methodCache.add(methodEntryRequest);
    }

    public Collection<String> getHandlerFrameworkMap() {
        Collection<String> result = new ArrayList<>();
        Collection<DefaultHandlerFramework> handlerOrFramework = debugManger.handlerOrFramework.values();
        for (DefaultHandlerFramework elem : handlerOrFramework) {
            HashMap<String, String> dataWrapper = elem.getDataWrapper();
            result.addAll(dataWrapper.values());
        }
        return result;
    }

    @Override
    public void run() {
        log.info("创建堆栈分析");

        Collection<String> handlerFrameworkMap = getHandlerFrameworkMap();
        if (handlerFrameworkMap.size() == 0) {
            log.info("无目标对象跟踪");
            return;
        }

//        开始跟踪堆栈
        for (String className : handlerFrameworkMap) {
            createMethodPoint(className);
        }

        EventQueue eventQueue = debugManger.vm.eventQueue();
        while (!stop) {
            try {
                EventSet oneOfEvent = eventQueue.remove(1000);
                if (oneOfEvent == null) {
                    continue;
                }
                for (Event eventElem : oneOfEvent) {
                    MethodEntryEvent methodEntryEvent = (MethodEntryEvent) eventElem;
                    ThreadReference thread = methodEntryEvent.thread();
                    int frameCount = thread.frameCount();
                    String name = methodEntryEvent.location().declaringType().name();
                    ApiStack apiStack = new ApiStack(name, new LinkedHashMap<>());
                    for (int i = 0; i < frameCount; i++) {
                        StackFrame frame = thread.frame(i);
                        Location location = frame.location();
                        apiStack.pushStack(location.declaringType().name(), location.method().name());
                    }
                    stackWebSocket.sendStack(apiStack);
                    debugManger.allStack.add(apiStack);
                }
                oneOfEvent.resume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        debugManger.vm.resume();
        log.info("堆栈分析线程结束");
    }

    public void endListen() {
        debugManger.vm.eventRequestManager().deleteEventRequests(methodCache);
        stop = true;
    }
}